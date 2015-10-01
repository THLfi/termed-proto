package fi.thl.termed.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import fi.thl.termed.serializer.Converters;

@Service
@Transactional
public class JsonCrudServiceImpl implements JsonCrudService {

  @PersistenceContext
  private EntityManager em;

  @javax.annotation.Resource
  private Map<String, Class> collectionClassMap;

  @Autowired
  private CrudService crudService;

  private Gson defaultGson;

  @PostConstruct
  public void init() {
    GsonBuilder b = new GsonBuilder().setPrettyPrinting();
    Converters.registerDateConverter(b);
    Converters.registerPropertyListConverter(b);
    Converters.registerConceptConverter(b, em);
    this.defaultGson = b.create();
  }

  @Override
  public JsonObject save(String collection, JsonObject object) {
    return save(collection, object, defaultGson);
  }

  @Override
  public JsonObject save(String collection, JsonObject object, Gson gson) {
    Class c = collectionClassMap.get(collection);
    return c == null ? new JsonObject() :
           gson.toJsonTree(crudService.save(c, gson.fromJson(object, c))).getAsJsonObject();
  }

  @Override
  public JsonObject get(String collection, String id) {
    return get(collection, id, defaultGson);
  }

  @Override
  public JsonObject get(String collection, String id, Gson gson) {
    Class c = collectionClassMap.get(collection);
    return c == null ? new JsonObject() :
           gson.toJsonTree(crudService.get(c, id)).getAsJsonObject();
  }

  @Override
  public JsonArray query(String collection, String query, int fst, int max, List<String> orderBy) {
    return query(collection, query, fst, max, orderBy, defaultGson);
  }

  @Override
  public JsonArray query(String collection, String query, int fst, int max, List<String> orderBy,
                         Gson gson) {
    Class c = collectionClassMap.get(collection);
    return c == null ? new JsonArray() :
           gson.toJsonTree(crudService.query(c, query, fst, max, orderBy)).getAsJsonArray();
  }

  @Override
  public void remove(String collection, String id) {
    Class c = collectionClassMap.get(collection);
    if (c != null) {
      crudService.remove(c, id);
    }
  }

}