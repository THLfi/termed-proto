package fi.thl.termed.controller;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import fi.thl.termed.model.Collection;
import fi.thl.termed.model.Concept;
import fi.thl.termed.model.PropertyResource;
import fi.thl.termed.model.PropertyValue;
import fi.thl.termed.model.Scheme;
import fi.thl.termed.repository.CollectionRepository;
import fi.thl.termed.repository.ConceptRepository;
import fi.thl.termed.repository.SchemeRepository;
import fi.thl.termed.util.SKOS;

import static com.hp.hpl.jena.rdf.model.ResourceFactory.createResource;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping(value = "/")
public class RdfExporter {

  @SuppressWarnings("all")
  private Logger log = LoggerFactory.getLogger(getClass());

  private SchemeRepository schemeRepository;
  private ConceptRepository conceptRepository;
  private CollectionRepository collectionRepository;

  private Map<String, Property> propertyMap;

  @Autowired
  public RdfExporter(SchemeRepository schemeRepository, ConceptRepository conceptRepository,
                     CollectionRepository collectionRepository) {
    this.schemeRepository = schemeRepository;
    this.conceptRepository = conceptRepository;
    this.collectionRepository = collectionRepository;
    this.propertyMap = ImmutableMap.<String, Property>builder()
        .put("prefLabel", SKOS.prefLabel)
        .put("altLabel", SKOS.altLabel)
        .put("hiddenLabel", SKOS.hiddenLabel)
        .put("note", SKOS.note)
        .put("definition", SKOS.definition)
        .put("example", SKOS.example)
        .build();
  }

  @RequestMapping(method = GET, value = "export/{schemeId}", produces = "text/turtle;charset=UTF-8")
  @Transactional
  public void exportTurtle(@PathVariable("schemeId") String schemeId, HttpServletResponse response)
      throws IOException {
    log.info("Exporting {}", schemeId);

    if (!schemeRepository.exists(schemeId)) {
      log.error("Scheme {} does not exist.", schemeId);
      return;
    }

    Model model = ModelFactory.createDefaultModel();
    model.setNsPrefix("skos", SKOS.getUri());

    exportScheme(schemeId, model);
    exportCollections(schemeId, model);
    exportConcepts(schemeId, model);

    response.setCharacterEncoding(Charsets.UTF_8.toString());
    model.write(response.getWriter(), "TTL");

    log.info("Done.");
  }

  private void exportScheme(String schemeId, Model model) {
    Scheme scheme = schemeRepository.findOne(schemeId);
    Resource r = createResource(scheme.getUri());
    model.add(r, RDF.type, SKOS.ConceptScheme);
    exportProperties(scheme, r, model);
  }

  private void exportProperties(PropertyResource propertyResource, Resource r, Model model) {
    for (PropertyValue value : propertyResource.getProperties()) {
      if (!Strings.isNullOrEmpty(value.getValue())) {
        model.add(r, propertyMap.get(value.getPropertyId()), value.getValue(), value.getLang());
      }
    }
  }

  private void exportCollections(String schemeId, Model model) {
    for (Collection collection : collectionRepository.findBySchemeId(schemeId)) {
      Resource r = createResource(collection.getUri());
      model.add(r, RDF.type, SKOS.Collection);
      model.add(r, SKOS.inScheme, createResource(collection.getScheme().getUri()));
      exportProperties(collection, r, model);
      if (collection.getMembers() != null) {
        for (Concept member : collection.getMembers()) {
          model.add(r, SKOS.member, createResource(member.getUri()));
        }
      }
    }
  }

  private void exportConcepts(String schemeId, Model model) {
    for (Concept concept : conceptRepository.findBySchemeId(schemeId)) {
      Resource r = createResource(concept.getUri());
      model.add(r, RDF.type, SKOS.Concept);
      model.add(r, SKOS.inScheme, createResource(concept.getScheme().getUri()));
      exportProperties(concept, r, model);
      if (concept.getBroader() != null) {
        for (Concept broader : concept.getBroader()) {
          model.add(r, SKOS.broader, createResource(broader.getUri()));
        }
      }
      if (concept.getNarrower() != null) {
        for (Concept narrower : concept.getNarrower()) {
          model.add(r, SKOS.narrower, createResource(narrower.getUri()));
        }
      }
      if (concept.getRelated() != null) {
        for (Concept related : concept.getRelated()) {
          model.add(r, SKOS.related, createResource(related.getUri()));
        }
      }
    }
  }

}
