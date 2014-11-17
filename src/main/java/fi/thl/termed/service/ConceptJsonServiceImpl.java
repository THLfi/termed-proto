package fi.thl.termed.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.thl.termed.dao.ConceptDao;
import fi.thl.termed.model.Concept;

@Service
@Transactional
public class ConceptJsonServiceImpl implements ConceptJsonService {

  private final ConceptDao conceptDao;
  private final Gson gson;

  @Autowired
  public ConceptJsonServiceImpl(ConceptDao conceptDao) {
    this.conceptDao = conceptDao;
    this.gson = new GsonBuilder().setPrettyPrinting().create();
  }

  @Override
  public JsonObject save(JsonObject concept) {
    return gson.toJsonTree(conceptDao.save(gson.fromJson(concept, Concept.class)))
        .getAsJsonObject();
  }

  @Override
  public JsonPrimitive saveAll(JsonArray concepts) {
    int saved = 0;

    for (JsonElement element : concepts) {
      save(element.getAsJsonObject());
      saved++;
    }

    return new JsonPrimitive(saved);
  }

  @Override
  public JsonObject get(String id) {
    return conceptDao.exists(id) ? gson.toJsonTree(conceptDao.get(id)).getAsJsonObject()
                                 : new JsonObject();
  }

  @Override
  public JsonArray query() {
    return gson.toJsonTree(conceptDao.query()).getAsJsonArray();
  }

  @Override
  public JsonArray query(int max) {
    return gson.toJsonTree(conceptDao.query(max)).getAsJsonArray();
  }

  @Override
  public JsonArray query(String query, int max) {
    return gson.toJsonTree(conceptDao.query(query, max)).getAsJsonArray();
  }

  @Override
  public void remove(String id) {
    conceptDao.remove(id);
  }

}
