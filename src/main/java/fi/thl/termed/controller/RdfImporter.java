package fi.thl.termed.controller;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
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

import fi.thl.termed.model.Concept;
import fi.thl.termed.model.Scheme;
import fi.thl.termed.repository.ConceptRepository;
import fi.thl.termed.repository.SchemeRepository;
import fi.thl.termed.util.SKOS;

import static org.apache.commons.codec.digest.DigestUtils.sha1Hex;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping(value = "/")
public class RdfImporter {

  @SuppressWarnings("all")
  private Logger log = LoggerFactory.getLogger(getClass());

  private SchemeRepository schemeRepository;
  private ConceptRepository conceptRepository;

  private List<String> languages = Lists.newArrayList("fi", "en", "sv");

  @Autowired
  public RdfImporter(SchemeRepository schemeRepository, ConceptRepository conceptRepository) {
    this.schemeRepository = schemeRepository;
    this.conceptRepository = conceptRepository;
  }

  @RequestMapping(method = POST, value = "import", consumes = "text/turtle;charset=UTF-8")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Transactional
  public void importTurtle(@RequestBody String input) {
    Model model = ModelFactory.createDefaultModel();
    model.read(new ByteArrayInputStream(input.getBytes()), null, "TTL");

    log.info("read {} statements", model.size());

    Scheme scheme = new Scheme("tero");
    schemeRepository.save(scheme);

    Map<String, Concept> concepts = Maps.newHashMap();

    for (Resource r : model.listResourcesWithProperty(RDF.type, SKOS.Concept).toList()) {
      Concept c = new Concept(sha1Hex(r.getURI()));
      addProperty(c, "prefLabel", model, r, SKOS.prefLabel);
      addProperties(c, "altLabel", model, r, SKOS.altLabel);
      addProperties(c, "hiddenLabel", model, r, SKOS.hiddenLabel);
      c.setScheme(scheme);
      concepts.put(c.getId(), c);
    }

    conceptRepository.save(concepts.values());

    for (Resource r : model.listResourcesWithProperty(RDF.type, SKOS.Concept).toList()) {
      Concept c = concepts.get(sha1Hex(r.getURI()));
      for (String broaderUri : getObjectValues(model, r, SKOS.broader)) {
        Concept broader = concepts.get(sha1Hex(broaderUri));
        if (broader != null) {
          c.setBroader(broader);
        }
      }
      for (String relatedUri : getObjectValues(model, r, SKOS.related)) {
        Concept related = concepts.get(sha1Hex(relatedUri));
        if (related != null) {
          related.addRelated(c);
        }
      }
    }

    conceptRepository.save(concepts.values());

    log.info("imported {} concepts", concepts.size());
  }

  private void addProperty(Concept c, String propertyId, Model model, Resource r, Property p) {
    for (String lang : languages) {
      String propertyValue = getLiteralValue(model, r, p, lang);
      if (!propertyValue.isEmpty()) {
        c.addProperty(propertyId, lang, propertyValue);
      }
    }
  }

  private void addProperties(Concept c, String propertyId, Model model, Resource r, Property p) {
    for (String lang : languages) {
      for (String propertyValue : getLiteralValues(model, r, p, lang)) {
        c.addProperty(propertyId, lang, propertyValue);
      }
    }
  }

  private List<String> getObjectValues(Model m, Resource r, Property p) {
    List<String> values = Lists.newArrayList();

    for (RDFNode n : m.listObjectsOfProperty(r, p).toList()) {
      if (n.isURIResource()) {
        values.add(n.asResource().getURI());
      }
    }

    return values;
  }

  private String getLiteralValue(Model m, Resource r, Property p, String lang) {
    return Joiner.on(", ").join(getLiteralValues(m, r, p, lang));
  }

  private List<String> getLiteralValues(Model m, Resource r, Property p, String lang) {
    List<String> values = Lists.newArrayList();

    for (RDFNode n : m.listObjectsOfProperty(r, p).toList()) {
      if (n.isLiteral()) {
        Literal literal = n.asLiteral();
        if (lang.equals(literal.getLanguage())) {
          values.add(literal.getString());
        }
      }
    }

    return values;
  }

}
