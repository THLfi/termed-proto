package fi.thl.termed.service;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import fi.thl.termed.domain.Collection;
import fi.thl.termed.domain.Concept;
import fi.thl.termed.domain.ConceptReference;
import fi.thl.termed.domain.ReferenceType;
import fi.thl.termed.domain.PropertyResource;
import fi.thl.termed.domain.PropertyValue;
import fi.thl.termed.domain.Scheme;
import fi.thl.termed.util.ListUtils;
import fi.thl.termed.util.LuceneQueryUtils;
import fi.thl.termed.util.SKOS;

import static com.google.common.base.Functions.forMap;
import static com.google.common.base.Predicates.in;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.hp.hpl.jena.rdf.model.ResourceFactory.createProperty;
import static com.hp.hpl.jena.rdf.model.ResourceFactory.createResource;

@Service
@Transactional
public class ConceptRdfServiceImpl implements ConceptRdfService {

  private static final String DEFAULT_NS = "http://meta.thl.fi/termed/";

  @SuppressWarnings("all")
  private Logger log = LoggerFactory.getLogger(getClass());

  private CrudService crudService;

  private Map<Property, String> propertyMap;
  private Map<Property, String> conceptReferenceTypeMap;
  private Set<String> acceptedLanguages;

  @Autowired
  public ConceptRdfServiceImpl(CrudService crudService) {
    this.crudService = crudService;

    this.propertyMap = Maps.newHashMap();
    for (fi.thl.termed.domain.Property p : crudService
        .queryCached(fi.thl.termed.domain.Property.class, LuceneQueryUtils.all())) {
      if (p.hasUri()) {
        propertyMap.put(ResourceFactory.createProperty(p.getUri()), p.getId());
      }
    }

    this.conceptReferenceTypeMap = Maps.newHashMap();
    for (ReferenceType p : crudService.queryCached(ReferenceType.class,
                                                          LuceneQueryUtils.all())) {
      if (p.hasUri()) {
        conceptReferenceTypeMap.put(ResourceFactory.createProperty(p.getUri()), p.getId());
      }
    }

    this.acceptedLanguages = Sets.newHashSet("", "fi", "en", "sv");
  }

  @Override
  public Model getScheme(String schemeId) {
    Model model = ModelFactory.createDefaultModel();
    model.setNsPrefix("skos", SKOS.getUri());

    addScheme(model, schemeId);
    addCollection(model, schemeId);
    addConcepts(model, schemeId);

    return model;
  }

  private void addScheme(Model model, String schemeId) {
    Scheme scheme = crudService.get(Scheme.class, schemeId);
    Resource r = createResource(uri(scheme));
    model.add(r, RDF.type, SKOS.ConceptScheme);
    exportProperties(scheme, r, model);
  }

  private void exportProperties(PropertyResource propertyResource, Resource r, Model model) {
    for (PropertyValue value : propertyResource.getProperties()) {
      if (!Strings.isNullOrEmpty(value.getValue())) {
        model.add(r, createProperty(uri(value.getProperty())), value.getValue(), value.getLang());
      }
    }
  }

  private void addCollection(Model model, String schemeId) {
    for (Collection collection : crudService
        .query(Collection.class, LuceneQueryUtils.term("scheme.id", schemeId))) {
      Resource r = createResource(uri(collection));
      model.add(r, RDF.type, SKOS.Collection);
      model.add(r, SKOS.inScheme, createResource(uri(collection.getScheme())));
      exportProperties(collection, r, model);
      if (collection.getMembers() != null) {
        for (Concept member : collection.getMembers()) {
          model.add(r, SKOS.member, createResource(uri(member)));
        }
      }
    }
  }

  private void addConcepts(Model model, String schemeId) {
    for (Concept concept : crudService
        .query(Concept.class, LuceneQueryUtils.term("scheme.id", schemeId))) {
      Resource r = createResource(uri(concept));
      model.add(r, RDF.type, SKOS.Concept);
      model.add(r, SKOS.inScheme, createResource(uri(concept.getScheme())));
      exportProperties(concept, r, model);

      if (concept.getReferences() != null) {
        for (ConceptReference reference : concept.getReferences()) {
          model.add(r, createProperty(uri(reference.getType())),
                    createResource(uri(reference.getTarget())));
        }
      }
      if (concept.getReferrers() != null) {
        for (ConceptReference referrer : concept.getReferrers()) {
          model.add(createResource(uri(referrer.getSource())),
                    createProperty(uri(referrer.getType())), r);
        }
      }
    }
  }

  private static String uri(Scheme scheme) {
    return scheme.hasUri() ? scheme.getUri() : DEFAULT_NS + "schemes/" + scheme.getId();
  }

  private static String uri(Concept concept) {
    return concept.hasUri() ? concept.getUri() :
           ensureTrailingDashOrHash(uri(concept.getScheme())) + "concepts/" + concept.getId();
  }

  private static String uri(Collection collection) {
    return collection.hasUri() ? collection.getUri() :
           ensureTrailingDashOrHash(uri(collection.getScheme())) + "collections/" + collection
               .getId();
  }

  private static String uri(fi.thl.termed.domain.Property property) {
    return property.hasUri() ? property.getUri() : DEFAULT_NS + "properties/" + property.getId();
  }

  private static String uri(ReferenceType referenceType) {
    return referenceType.hasUri() ? referenceType.getUri() :
           DEFAULT_NS + "conceptReferenceTypes/" + referenceType.getId();
  }

  private static String ensureTrailingDashOrHash(String str) {
    return str.endsWith("/") || str.endsWith("#") ? str : str + "/";
  }

  private Function<Statement, PropertyValue> statementsToPropertyValues =
      new Function<Statement, PropertyValue>() {
        @Override
        public PropertyValue apply(Statement s) {
          return new PropertyValue(propertyMap.get(s.getPredicate()),
                                   s.getLanguage(),
                                   s.getLiteral().getString());
        }
      };

  private Predicate<Statement> isAcceptedLiteralStatement = new Predicate<Statement>() {
    @Override
    public boolean apply(Statement s) {
      RDFNode object = s.getObject();
      return propertyMap.containsKey(s.getPredicate()) && object.isLiteral()
             && acceptedLanguages.contains(object.asLiteral().getLanguage());
    }
  };

  private Predicate<Statement> isAcceptedObjectStatement = new Predicate<Statement>() {
    @Override
    public boolean apply(Statement s) {
      return conceptReferenceTypeMap.containsKey(s.getPredicate()) && s.getObject().isURIResource();
    }
  };

  @Override
  public void saveScheme(Model model) {
    saveScheme(UUID.randomUUID().toString(), model);
  }

  @Override
  public void saveScheme(String schemeId, Model model) {
    Map<String, String> uriIdMap = Maps.newHashMap();

    log.info("read {} statements", model.size());

    Scheme scheme = readScheme(model, schemeId);
    crudService.save(Scheme.class, scheme);

    Map<String, Concept> concepts = readConcepts(model, scheme, uriIdMap);
    crudService.save(Concept.class, concepts.values());
    linkConcepts(model, concepts, uriIdMap);
    crudService.save(Concept.class, concepts.values());

    Map<String, Collection> collections =
        readCollections(model, scheme, concepts, uriIdMap);
    crudService.save(Collection.class, collections.values());

    log.info("imported {} concepts", concepts.size());
    log.info("imported {} collections", collections.size());
  }

  private Scheme readScheme(Model model, String schemeId) {
    Scheme scheme = new Scheme(schemeId);

    Resource r = instanceOf(model, SKOS.ConceptScheme);
    if (r != null) {
      scheme.setUri(r.getURI());
      scheme.setProperties(readProperties(model, r));
    }

    return scheme;
  }

  private String getId(String uri, Map<String, String> uriIdMap) {
    if (!uriIdMap.containsKey(uri)) {
      uriIdMap.put(uri, UUID.randomUUID().toString());
    }
    return uriIdMap.get(uri);
  }

  private Resource instanceOf(Model model, Resource rdfClass) {
    List<Resource> instances = instancesOf(model, rdfClass);
    return !ListUtils.isNullOrEmpty(instances) ? instances.get(0) : null;
  }

  private List<Resource> instancesOf(Model model, Resource rdfClass) {
    return model.listResourcesWithProperty(RDF.type, rdfClass).toList();
  }

  private Map<String, Concept> readConcepts(Model model, Scheme scheme,
                                            Map<String, String> uriIdMap) {
    Map<String, Concept> concepts = Maps.newHashMap();

    for (Resource r : instancesOf(model, SKOS.Concept)) {
      Concept concept = new Concept(getId(r.getURI(), uriIdMap));
      concept.setUri(r.getURI());
      concept.setScheme(scheme);
      concept.setProperties(readProperties(model, r));
      concepts.put(concept.getId(), concept);
    }

    return concepts;
  }

  private void linkConcepts(Model model, Map<String, Concept> concepts,
                            Map<String, String> uriIdMap) {
    for (Resource r : instancesOf(model, SKOS.Concept)) {
      Concept source = concepts.get(getId(r.getURI(), uriIdMap));
      for (Statement s : filter(model.listStatements(r, null, (RDFNode) null).toList(),
                                isAcceptedObjectStatement)) {
        ReferenceType referenceType =
            new ReferenceType(conceptReferenceTypeMap.get(s.getPredicate()));
        Concept target = concepts.get(getId(s.getObject().asResource().getURI(), uriIdMap));
        source.addReferences(referenceType, target);
      }
    }
  }

  private Map<String, Collection> readCollections(Model model,
                                                  Scheme scheme,
                                                  Map<String, Concept> concepts,
                                                  Map<String, String> uriIdMap) {
    Map<String, Collection> collections = Maps.newHashMap();

    for (Resource r : instancesOf(model, SKOS.Collection)) {
      Collection collection = new Collection(getId(r.getURI(), uriIdMap));
      collection.setUri(r.getURI());
      collection.setScheme(scheme);
      collection.setProperties(readProperties(model, r));
      collection.setMembers(readObjects(model, r, SKOS.member, concepts, uriIdMap));
      collections.put(collection.getId(), collection);
    }

    return collections;
  }

  private <T> List<T> readObjects(Model model, Resource r, Property p, Map<String, T> values,
                                  Map<String, String> uriIdMap) {
    Iterable<RDFNode> objects = model.listObjectsOfProperty(r, p).toList();
    Iterable<String> objectIds = transform(objects, new UriToId(uriIdMap));
    Iterable<String> existingObjectIds = filter(objectIds, in(values.keySet()));
    Iterable<T> populatedObjects = transform(existingObjectIds, forMap(values));
    return newArrayList(populatedObjects);
  }

  private List<PropertyValue> readProperties(Model model, Resource r) {
    return newArrayList(transform(filter(model.listStatements(r, null, (RDFNode) null).toList(),
                                         isAcceptedLiteralStatement), statementsToPropertyValues));
  }

  private class UriToId implements Function<RDFNode, String> {

    private Map<String, String> uriIdMap;

    public UriToId(Map<String, String> uriIdMap) {
      this.uriIdMap = uriIdMap;
    }

    @Override
    public String apply(RDFNode input) {
      String uri = input.isURIResource() ? input.asResource().getURI() : input.toString();
      return getId(uri, uriIdMap);
    }
  }

}
