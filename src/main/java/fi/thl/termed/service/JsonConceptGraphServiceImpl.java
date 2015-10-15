package fi.thl.termed.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.thl.termed.model.Concept;
import fi.thl.termed.serializer.Converters;
import fi.thl.termed.util.ConceptGraphUtils;

@Service
@Transactional(readOnly = true)
public class JsonConceptGraphServiceImpl implements JsonConceptGraphService {

  private CrudService crudService;
  private ConceptTreeService conceptTreeService;

  private Gson fastGson;

  @Autowired
  public JsonConceptGraphServiceImpl(CrudService crudService,
                                     ConceptTreeService conceptTreeService) {
    this.crudService = crudService;
    this.conceptTreeService = conceptTreeService;

    GsonBuilder b = new GsonBuilder().setPrettyPrinting();
    Converters.registerDateConverter(b);
    Converters.registerPropertyListConverter(b);
    Converters.registerTruncatingConceptConverter(b);
    this.fastGson = b.create();
  }

  @Override
  public JsonArray getConceptBroaderPaths(String id) {
    Concept concept = crudService.get(Concept.class, id);
    return concept == null ? new JsonArray() :
           fastGson.toJsonTree(ConceptGraphUtils.collectBroaderPaths(concept)).getAsJsonArray();
  }

  @Override
  public JsonArray getConceptPartOfPaths(String id) {
    Concept concept = crudService.get(Concept.class, id);
    return concept == null ? new JsonArray() :
           fastGson.toJsonTree(ConceptGraphUtils.collectPartOfPaths(concept)).getAsJsonArray();
  }

  @Override
  public JsonArray getConceptJsTrees(String id) {
    Concept concept = crudService.get(Concept.class, id);
    return concept == null ? new JsonArray() :
           fastGson.toJsonTree(
               conceptTreeService.getConceptNarrowerJsTree(concept)).getAsJsonArray();
  }

}
