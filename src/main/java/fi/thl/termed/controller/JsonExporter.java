package fi.thl.termed.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Date;

import fi.thl.termed.model.Concept;
import fi.thl.termed.model.SerializedConcept;
import fi.thl.termed.repository.ConceptRepository;
import fi.thl.termed.repository.SchemeRepository;
import fi.thl.termed.serializer.ConceptLoadingConverter;
import fi.thl.termed.serializer.DateConverter;
import fi.thl.termed.serializer.PropertyListConverter;

import static fi.thl.termed.serializer.ConvertingSerializer.registerConverter;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping(value = "/")
public class JsonExporter {

  @SuppressWarnings("all")
  private Logger log = LoggerFactory.getLogger(getClass());

  private SchemeRepository schemeRepository;
  private ConceptRepository conceptRepository;
  private Gson gson;

  @Autowired
  public JsonExporter(SchemeRepository schemeRepository, ConceptRepository conceptRepository) {
    this.schemeRepository = schemeRepository;
    this.conceptRepository = conceptRepository;

    GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
    registerConverter(builder, Date.class, String.class, new DateConverter());
    registerConverter(builder,
                      PropertyListConverter.PROPERTY_LIST_TYPE,
                      PropertyListConverter.PROPERTY_MAP_TYPE,
                      new PropertyListConverter());
    registerConverter(builder, Concept.class, SerializedConcept.class,
                      new ConceptLoadingConverter());

    this.gson = builder.create();
  }

  @RequestMapping(method = GET, value = "export/{schemeId}/json",
      produces = "application/json;charset=UTF-8")
  @ResponseBody
  @Transactional
  public JsonArray exportJson(@PathVariable("schemeId") String schemeId)
      throws IOException {
//    log.info("Exporting {}", schemeId);
//
//    if (!schemeRepository.exists(schemeId)) {
//      log.error("Scheme {} does not exist.", schemeId);
//      return new JsonArray();
//    }
//
//    return gson.toJsonTree(conceptRepository.findBySchemeIdAndBroaderIsNull(
//        schemeId)).getAsJsonArray();
    return null;
  }


}
