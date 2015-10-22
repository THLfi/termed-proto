package fi.thl.termed.service;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.reflections.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import fi.thl.termed.repository.CrudRepository;
import fi.thl.termed.serializer.Converters;

@Service
@Transactional
public class JsonCrudServiceImpl implements JsonCrudService {

  @PersistenceContext
  private EntityManager em;
  private Gson defaultGson;

  private CrudService crudService;
  private Map<String, Class> collectionClassMap;

  @Autowired
  public JsonCrudServiceImpl(CrudService crudService, List<CrudRepository> crudRepositoryList) {
    this.crudService = crudService;
    this.collectionClassMap = Maps.newHashMap();

    for (CrudRepository repository : crudRepositoryList) {
      String name = getRepositoryName(repository);
      this.collectionClassMap.put(name, repository.getType());
    }
  }

  @PostConstruct
  public void init() {
    GsonBuilder b = new GsonBuilder().setPrettyPrinting();
    Converters.registerDateConverter(b);
    Converters.registerPropertyListConverter(b);
    Converters.registerConceptConverter(b, em);
    this.defaultGson = b.create();
  }

  @SuppressWarnings("unchecked")
  private String getRepositoryName(CrudRepository repository) {
    for (Annotation annotation : ReflectionUtils.getAllAnnotations(repository.getClass())) {
      if (annotation instanceof Repository) {
        return ((Repository) annotation).value();
      }
    }

    return repository.getType().getSimpleName().toLowerCase();
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
  public JsonArray save(String collection, JsonArray array) {
    return save(collection, array, defaultGson);
  }

  @Override
  public JsonArray save(String collection, JsonArray array, Gson gson) {
    JsonArray saved = new JsonArray();
    for (JsonElement element : array) {
      saved.add(save(collection, element.getAsJsonObject(), gson));
    }
    return saved;
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
  public JsonArray queryCached(String collection, String query, int fst, int max,
                               List<String> orderBy) {
    return queryCached(collection, query, fst, max, orderBy, defaultGson);
  }

  @Override
  public JsonArray queryCached(String collection, String query, int fst, int max,
                               List<String> orderBy,
                               Gson gson) {
    Class c = collectionClassMap.get(collection);
    return c == null ? new JsonArray() :
           gson.toJsonTree(crudService.queryCached(c, query, fst, max, orderBy)).getAsJsonArray();
  }

  @Override
  public void remove(String collection, String id) {
    Class c = collectionClassMap.get(collection);
    if (c != null) {
      crudService.remove(c, id);
    }
  }

}
