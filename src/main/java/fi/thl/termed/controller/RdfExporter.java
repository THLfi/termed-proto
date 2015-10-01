package fi.thl.termed.controller;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
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

import javax.servlet.http.HttpServletResponse;

import fi.thl.termed.model.Collection;
import fi.thl.termed.model.Concept;
import fi.thl.termed.model.ConceptReference;
import fi.thl.termed.model.ConceptReferenceType;
import fi.thl.termed.model.Property;
import fi.thl.termed.model.PropertyResource;
import fi.thl.termed.model.PropertyValue;
import fi.thl.termed.model.Scheme;
import fi.thl.termed.service.CrudService;
import fi.thl.termed.util.SKOS;

import static com.hp.hpl.jena.rdf.model.ResourceFactory.createProperty;
import static com.hp.hpl.jena.rdf.model.ResourceFactory.createResource;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping(value = "/")
public class RdfExporter {

  private static final String defaultNs = "http://meta.thl.fi/termed/";

  @SuppressWarnings("all")
  private Logger log = LoggerFactory.getLogger(getClass());

  @Autowired
  private CrudService crudService;

  @RequestMapping(method = GET, value = "export/{schemeId}", produces = "text/turtle;charset=UTF-8")
  @Transactional
  public void exportTurtle(@PathVariable("schemeId") String schemeId, HttpServletResponse response)
      throws IOException {
    log.info("Exporting {}", schemeId);

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

  private void exportCollections(String schemeId, Model model) {
    for (Collection collection : crudService
        .query(Collection.class, "scheme.id:" + schemeId, 0, -1, null)) {
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

  private void exportConcepts(String schemeId, Model model) {
    for (Concept concept : crudService.query(Concept.class, "scheme.id:" + schemeId, 0, -1, null)) {
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
    return scheme.hasUri() ? scheme.getUri() : defaultNs + "schemes/" + scheme.getId();
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

  private static String uri(Property property) {
    return property.hasUri() ? property.getUri() : defaultNs + "properties/" + property.getId();
  }

  private static String uri(ConceptReferenceType referenceType) {
    return referenceType.hasUri() ? referenceType.getUri() :
           defaultNs + "conceptReferenceTypes/" + referenceType.getId();
  }

  private static String ensureTrailingDashOrHash(String str) {
    return str.endsWith("/") || str.endsWith("#") ? str : str + "/";
  }

}
