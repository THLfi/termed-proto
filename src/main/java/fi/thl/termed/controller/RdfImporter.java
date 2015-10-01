package fi.thl.termed.controller;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import fi.thl.termed.model.Collection;
import fi.thl.termed.model.Concept;
import fi.thl.termed.model.ConceptReferenceType;
import fi.thl.termed.model.PropertyValue;
import fi.thl.termed.model.Scheme;
import fi.thl.termed.model.SchemeResource;
import fi.thl.termed.service.CrudService;
import fi.thl.termed.util.SKOS;

import static com.google.common.base.Functions.forMap;
import static com.google.common.base.Predicates.in;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping(value = "/")
public class RdfImporter {

  @SuppressWarnings("all")
  private Logger log = LoggerFactory.getLogger(getClass());

  private CrudService crudService;

  private Map<Property, String> propertyMap;
  private Map<Property, String> conceptReferenceTypeMap;
  private Set<String> acceptedLanguages;

  @Autowired
  public RdfImporter(CrudService crudService) {
    this.crudService = crudService;

    this.propertyMap = Maps.newHashMap();
    for (fi.thl.termed.model.Property p : crudService
        .query(fi.thl.termed.model.Property.class, "", 0, -1, null)) {
      if (p.hasUri()) {
        propertyMap.put(ResourceFactory.createProperty(p.getUri()), p.getId());
      }
    }

    this.conceptReferenceTypeMap = Maps.newHashMap();
    for (ConceptReferenceType p : crudService.query(ConceptReferenceType.class, "", 0, -1, null)) {
      if (p.hasUri()) {
        conceptReferenceTypeMap.put(ResourceFactory.createProperty(p.getUri()), p.getId());
      }
    }

    this.acceptedLanguages = Sets.newHashSet("", "fi", "en", "sv");
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

  @RequestMapping(method = POST, value = "import", consumes = "text/turtle;charset=UTF-8")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Transactional
  public void importTurtle(@RequestBody String input) {
    Model model = ModelFactory.createDefaultModel();
    model.read(new ByteArrayInputStream(input.getBytes(Charsets.UTF_8)), null, "TTL");

    Map<String, String> uriIdMap = Maps.newHashMap();

    log.info("read {} statements", model.size());

    Map<String, Scheme> schemes = readSchemes(model, uriIdMap);
    crudService.save(Scheme.class, schemes.values());

    Map<String, Concept> concepts = readConcepts(model, schemes, uriIdMap);
    crudService.save(Concept.class, concepts.values());
    linkConcepts(model, concepts, uriIdMap);
    crudService.save(Concept.class, concepts.values());

    Map<String, Collection> collections = readCollections(model, concepts, schemes, uriIdMap);
    crudService.save(Collection.class, collections.values());

    log.info("imported {} schemes", schemes.size());
    log.info("imported {} concepts", concepts.size());
    log.info("imported {} collections", collections.size());
  }

  private Map<String, Scheme> readSchemes(Model model, Map<String, String> uriIdMap) {
    Map<String, Scheme> schemes = Maps.newHashMap();

    for (Resource r : instancesOf(model, SKOS.ConceptScheme)) {
      Scheme scheme = new Scheme(getId(r.getURI(), uriIdMap));
      scheme.setUri(r.getURI());
      scheme.setProperties(readProperties(model, r));
      schemes.put(scheme.getId(), scheme);
    }

    return schemes;
  }

  private String getId(String uri, Map<String, String> uriIdMap) {
    if (!uriIdMap.containsKey(uri)) {
      uriIdMap.put(uri, UUID.randomUUID().toString());
    }
    return uriIdMap.get(uri);
  }

  private List<Resource> instancesOf(Model model, Resource rdfClass) {
    return model.listResourcesWithProperty(RDF.type, rdfClass).toList();
  }

  private Map<String, Concept> readConcepts(Model model, Map<String, Scheme> schemes,
                                            Map<String, String> uriIdMap) {
    Map<String, Concept> concepts = Maps.newHashMap();

    for (Resource r : instancesOf(model, SKOS.Concept)) {
      Concept concept = new Concept(getId(r.getURI(), uriIdMap));
      concept.setUri(r.getURI());
      concept.setProperties(readProperties(model, r));
      concept.setScheme(readObject(model, r, SKOS.inScheme, schemes, uriIdMap));
      if (concept.getScheme() == null && !schemes.isEmpty()) {
        assignAnyScheme(concept, schemes);
      }
      concepts.put(concept.getId(), concept);
    }

    return concepts;
  }

  private void assignAnyScheme(SchemeResource concept, Map<String, Scheme> schemes) {
    Scheme scheme = schemes.values().iterator().next();
    log.info("no scheme found for {}, using {}", concept, scheme);
    concept.setScheme(scheme);
  }

  private void linkConcepts(Model model, Map<String, Concept> concepts,
                            Map<String, String> uriIdMap) {
    for (Resource r : instancesOf(model, SKOS.Concept)) {
      Concept source = concepts.get(getId(r.getURI(), uriIdMap));
      for (Statement s : filter(model.listStatements(r, null, (RDFNode) null).toList(),
                                isAcceptedObjectStatement)) {
        ConceptReferenceType conceptReferenceType =
            new ConceptReferenceType(conceptReferenceTypeMap.get(s.getPredicate()));
        Concept target = concepts.get(getId(s.getObject().asResource().getURI(), uriIdMap));
        source.addReferences(conceptReferenceType, target);
      }
    }
  }

  private Map<String, Collection> readCollections(Model model, Map<String, Concept> concepts,
                                                  Map<String, Scheme> schemes,
                                                  Map<String, String> uriIdMap) {
    Map<String, Collection> collections = Maps.newHashMap();

    for (Resource r : instancesOf(model, SKOS.Collection)) {
      Collection collection = new Collection(getId(r.getURI(), uriIdMap));
      collection.setUri(r.getURI());
      collection.setProperties(readProperties(model, r));
      collection.setMembers(readObjects(model, r, SKOS.member, concepts, uriIdMap));
      collection.setScheme(readObject(model, r, SKOS.inScheme, schemes, uriIdMap));
      if (collection.getScheme() == null) {
        inferSchemeFromMembers(collection, collection.getMembers());
      }
      if (collection.getScheme() == null) {
        assignAnyScheme(collection, schemes);
      }
      collections.put(collection.getId(), collection);
    }

    return collections;
  }

  private void inferSchemeFromMembers(Collection collection, List<Concept> members) {
    for (Concept member : members) {
      if (member.getScheme() != null) {
        collection.setScheme(member.getScheme());
        break;
      }
    }
  }

  private <T> T readObject(Model model, Resource r, Property p, Map<String, T> values,
                           Map<String, String> uriIdMap) {
    List<T> objects = readObjects(model, r, p, values, uriIdMap);
    return !objects.isEmpty() ? objects.get(0) : null;
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
