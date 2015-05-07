package fi.thl.termed.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Set;

import fi.thl.termed.model.Concept;
import fi.thl.termed.model.Scheme;
import fi.thl.termed.model.SchemeResource;
import fi.thl.termed.repository.ConceptRepository;
import fi.thl.termed.repository.SchemeRepository;
import fi.thl.termed.serializer.DateConverter;
import fi.thl.termed.serializer.PropertyListConverter;
import fi.thl.termed.util.ConceptGraphUtils;
import fi.thl.termed.util.ListUtils;

import static fi.thl.termed.serializer.ConvertingSerializer.registerConverter;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping(value = "/")
public class JsonImporter {

  private static final Type CONCEPT_LIST_TYPE = new TypeToken<List<Concept>>() {
  }.getType();

  @SuppressWarnings("all")
  private Logger log = LoggerFactory.getLogger(getClass());

  private SchemeRepository schemeRepository;
  private ConceptRepository conceptRepository;
  private Gson gson;

  @Autowired
  public JsonImporter(SchemeRepository schemeRepository,
                      ConceptRepository conceptRepository) {
    this.schemeRepository = schemeRepository;
    this.conceptRepository = conceptRepository;

    GsonBuilder builder = new GsonBuilder();
    registerConverter(builder, Date.class, String.class, new DateConverter());
    registerConverter(builder,
                      PropertyListConverter.PROPERTY_LIST_TYPE,
                      PropertyListConverter.PROPERTY_MAP_TYPE,
                      new PropertyListConverter());
    this.gson = builder.create();
  }

  @RequestMapping(method = POST,
      value = "import/schemes/{schemeId}/concepts",
      consumes = "application/json;charset=UTF-8")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Transactional
  public void importConcepts(@PathVariable("schemeId") String schemeId,
                             @RequestBody JsonArray input) {
    if (!schemeRepository.exists(schemeId)) {
      log.error("Scheme {} does not exist.", schemeId);
      return;
    }

    List<Concept> rootConcepts = gson.fromJson(input, CONCEPT_LIST_TYPE);

    Scheme scheme = schemeRepository.findOne(schemeId);
    Set<Concept> allConcepts =
        ConceptGraphUtils.collectConcepts(rootConcepts, ConceptGraphUtils.getAllNeighboursFunction);

    log.info("Importing {} concepts", allConcepts.size());
    log.info("Saving concept properties");

    // save all identities
    for (Concept concept : allConcepts) {
      concept.ensureId();
      concept.setScheme(scheme);
      if (!conceptRepository.exists(concept.getId())) {
        conceptRepository.save(new Concept(new SchemeResource(concept)));
      }
    }

    log.info("Linking concepts");

    // populate stored fields
    for (Concept concept : allConcepts) {
      for (Concept narrower : ListUtils.nullToEmpty(concept.getNarrower())) {
        narrower.addBroader(concept);
      }
      for (Concept instance : ListUtils.nullToEmpty(concept.getInstances())) {
        instance.addType(concept);
      }
      for (Concept part : ListUtils.nullToEmpty(concept.getParts())) {
        part.addPartOf(concept);
      }
    }

    for (Concept concept: rootConcepts) {
      saveConceptTree(concept);
    }
  }

  private void saveConceptTree(Concept concept) {
    conceptRepository.save(concept);
    for (Concept c : ListUtils.nullToEmpty(concept.getNarrower())) {
      saveConceptTree(c);
    }
    for (Concept c : ListUtils.nullToEmpty(concept.getParts())) {
      saveConceptTree(c);
    }
    for (Concept c : ListUtils.nullToEmpty(concept.getInstances())) {
      saveConceptTree(c);
    }
  }

}
