package fi.thl.termed.controller;

import com.google.common.collect.Lists;
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

import java.util.Date;
import java.util.List;

import fi.thl.termed.model.Concept;
import fi.thl.termed.model.Scheme;
import fi.thl.termed.repository.ConceptRepository;
import fi.thl.termed.repository.SchemeRepository;
import fi.thl.termed.util.GsonDateConverter;
import fi.thl.termed.util.PropertyValueListTransformer;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping(value = "/")
public class JsonImporter {

  @SuppressWarnings("all")
  private Logger log = LoggerFactory.getLogger(getClass());
  private TypeToken<List<Concept>> conceptListTypeToken = new TypeToken<List<Concept>>() {
  };
  private Gson gson = new GsonBuilder()
      .registerTypeAdapter(Date.class, new GsonDateConverter())
      .registerTypeAdapter(PropertyValueListTransformer.PROPERTY_LIST_TYPE,
                           new PropertyValueListTransformer()).create();


  private SchemeRepository schemeRepository;
  private ConceptRepository conceptRepository;

  @Autowired
  public JsonImporter(SchemeRepository schemeRepository,
                      ConceptRepository conceptRepository) {
    this.schemeRepository = schemeRepository;
    this.conceptRepository = conceptRepository;
  }

  @RequestMapping(method = POST, value = "import/schemes/{schemeId}/concepts",
      consumes = "application/json;charset=UTF-8")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Transactional
  public void importConcepts(@PathVariable("schemeId") String schemeId,
                             @RequestBody JsonArray input) {
    if (!schemeRepository.exists(schemeId)) {
      log.error("Scheme {} does not exist.", schemeId);
      return;
    }

    List<Concept> rootConcepts = gson.fromJson(input, conceptListTypeToken.getType());
    Scheme scheme = schemeRepository.findOne(schemeId);

    for (Concept concept : rootConcepts) {
      saveConceptTree(scheme, concept);
    }
  }

  private void saveConceptTree(Scheme scheme, Concept concept) {
    concept.setScheme(scheme);
    conceptRepository.save(concept);

    if (concept.getNarrower() != null) {
      for (Concept narrower : concept.getNarrower()) {
        narrower.setBroader(Lists.newArrayList(concept));
        saveConceptTree(scheme, narrower);
      }
    }
  }

}