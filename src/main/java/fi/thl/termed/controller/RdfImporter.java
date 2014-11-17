package fi.thl.termed.controller;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

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

import fi.thl.termed.model.Collection;
import fi.thl.termed.model.Concept;
import fi.thl.termed.model.PropertyValue;
import fi.thl.termed.model.Scheme;
import fi.thl.termed.model.SchemePropertyResource;
import fi.thl.termed.repository.CollectionRepository;
import fi.thl.termed.repository.ConceptRepository;
import fi.thl.termed.repository.SchemeRepository;
import fi.thl.termed.util.SKOS;

import static com.google.common.base.Functions.forMap;
import static com.google.common.base.Predicates.in;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.codec.digest.DigestUtils.sha1Hex;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping(value = "/")
public class RdfImporter {

  @SuppressWarnings("all")
  private Logger log = LoggerFactory.getLogger(getClass());

  private SchemeRepository schemeRepository;
  private ConceptRepository conceptRepository;
  private CollectionRepository collectionRepository;

  private Map<Property, String> propertyMap = ImmutableMap.<Property, String>builder()
      .put(DC.title, "prefLabel")
      .put(RDFS.label, "prefLabel")
      .put(SKOS.prefLabel, "prefLabel")
      .put(SKOS.altLabel, "altLabel")
      .put(SKOS.hiddenLabel, "hiddenLabel")
      .put(SKOS.note, "note")
      .put(SKOS.definition, "definition")
      .put(SKOS.example, "example")
      .build();

  private Set<String> acceptedLanguages = Sets.newHashSet("", "fi", "en", "sv");

  private Function<RDFNode, String> nodeUriToId =
      new Function<RDFNode, String>() {
        @Override
        public String apply(RDFNode s) {
          return sha1Hex(s.isURIResource() ? s.asResource().getURI() : s.toString());
        }
      };

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

  @Autowired
  public RdfImporter(SchemeRepository schemeRepository, ConceptRepository conceptRepository,
                     CollectionRepository collectionRepository) {
    this.schemeRepository = schemeRepository;
    this.conceptRepository = conceptRepository;
    this.collectionRepository = collectionRepository;
  }

  @RequestMapping(method = POST, value = "import", consumes = "text/turtle;charset=UTF-8")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Transactional
  public void importTurtle(@RequestBody String input) {
    Model model = ModelFactory.createDefaultModel();
    model.read(new ByteArrayInputStream(input.getBytes()), null, "TTL");

    log.info("read {} statements", model.size());

    Map<String, Scheme> schemes = readSchemes(model);
    schemeRepository.save(schemes.values());

    Map<String, Concept> concepts = readConcepts(model, schemes);
    conceptRepository.save(concepts.values());
    linkConcepts(model, concepts);
    conceptRepository.save(concepts.values());

    Map<String, Collection> collections = readCollections(model, concepts, schemes);
    collectionRepository.save(collections.values());

    log.info("imported {} schemes", schemes.size());
    log.info("imported {} concepts", concepts.size());
    log.info("imported {} collections", collections.size());
  }

  private Map<String, Scheme> readSchemes(Model model) {
    Map<String, Scheme> schemes = Maps.newHashMap();

    for (Resource r : instancesOf(model, SKOS.ConceptScheme)) {
      Scheme scheme = new Scheme(sha1Hex(r.getURI()));
      scheme.setProperties(readProperties(model, r));
      schemes.put(scheme.getId(), scheme);
    }

    return schemes;
  }

  private List<Resource> instancesOf(Model model, Resource rdfClass) {
    return model.listResourcesWithProperty(RDF.type, rdfClass).toList();
  }

  private Map<String, Concept> readConcepts(Model model, Map<String, Scheme> schemes) {
    Map<String, Concept> concepts = Maps.newHashMap();

    for (Resource r : instancesOf(model, SKOS.Concept)) {
      Concept concept = new Concept(sha1Hex(r.getURI()));
      concept.setProperties(readProperties(model, r));
      concept.setScheme(readObject(model, r, SKOS.inScheme, schemes));
      if (concept.getScheme() == null && !schemes.isEmpty()) {
        assignAnyScheme(concept, schemes);
      }
      concepts.put(concept.getId(), concept);
    }

    return concepts;
  }

  private void assignAnyScheme(SchemePropertyResource concept, Map<String, Scheme> schemes) {
    Scheme scheme = schemes.values().iterator().next();
    log.info("no scheme found for {}, using {}", concept, scheme);
    concept.setScheme(scheme);
  }

  private void linkConcepts(Model model, Map<String, Concept> concepts) {
    for (Resource r : instancesOf(model, SKOS.Concept)) {
      Concept concept = concepts.get(sha1Hex(r.getURI()));
      concept.setBroader(readObject(model, r, SKOS.broader, concepts));
      concept.setNarrower(readObjects(model, r, SKOS.narrower, concepts));
      concept.setRelated(readObjects(model, r, SKOS.related, concepts));
    }
  }

  private Map<String, Collection> readCollections(Model model, Map<String, Concept> concepts,
                                                  Map<String, Scheme> schemes) {
    Map<String, Collection> collections = Maps.newHashMap();

    for (Resource r : instancesOf(model, SKOS.Collection)) {
      Collection collection = new Collection(sha1Hex(r.getURI()));
      collection.setProperties(readProperties(model, r));
      collection.setMembers(readObjects(model, r, SKOS.member, concepts));
      collection.setScheme(readObject(model, r, SKOS.inScheme, schemes));
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

  private <T> T readObject(Model model, Resource r, Property p, Map<String, T> values) {
    List<T> objects = readObjects(model, r, p, values);
    return !objects.isEmpty() ? objects.get(0) : null;
  }

  private <T> List<T> readObjects(Model model, Resource r, Property p, Map<String, T> values) {
    Iterable<RDFNode> objects = model.listObjectsOfProperty(r, p).toList();
    Iterable<String> objectIds = transform(objects, nodeUriToId);
    Iterable<String> existingObjectIds = filter(objectIds, in(values.keySet()));
    Iterable<T> populatedObjects = transform(existingObjectIds, forMap(values));
    return newArrayList(populatedObjects);
  }

  private List<PropertyValue> readProperties(Model model, Resource r) {
    return newArrayList(transform(filter(model.listStatements(r, null, (RDFNode) null).toList(),
                                         isAcceptedLiteralStatement), statementsToPropertyValues));
  }

}
