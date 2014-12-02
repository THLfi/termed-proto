package fi.thl.termed.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import fi.thl.termed.model.Collection;
import fi.thl.termed.model.Concept;
import fi.thl.termed.model.Scheme;
import fi.thl.termed.repository.CollectionRepository;
import fi.thl.termed.repository.ConceptIndex;
import fi.thl.termed.repository.ConceptRepository;
import fi.thl.termed.repository.SchemeRepository;
import fi.thl.termed.util.ConceptTransformer;
import fi.thl.termed.util.ConceptUtils;
import fi.thl.termed.util.HibernateProxyTypeAdapterFactory;
import fi.thl.termed.util.LuceneQueryUtils;
import fi.thl.termed.util.PropertyValueListTransformer;

@Service
@Transactional
public class JsonServiceImpl implements JsonService {

  private SchemeRepository schemeRepository;
  private CollectionRepository collectionRepository;
  private ConceptRepository conceptRepository;
  private ConceptIndex conceptIndex;
  private Gson gson;

  @PersistenceContext
  private EntityManager em;

  @Autowired
  public JsonServiceImpl(SchemeRepository schemeRepository,
                         CollectionRepository collectionRepository,
                         ConceptRepository conceptRepository) {
    this.schemeRepository = schemeRepository;
    this.collectionRepository = collectionRepository;
    this.conceptRepository = conceptRepository;
    // init rest in post construct to get entity manager
  }

  @PostConstruct
  public void init() {
    this.gson = new GsonBuilder().setPrettyPrinting()
        .registerTypeAdapter(Concept.class, new ConceptTransformer(em))
        .registerTypeAdapterFactory(new HibernateProxyTypeAdapterFactory())
        .registerTypeAdapter(PropertyValueListTransformer.PROPERTY_LIST_TYPE,
                             new PropertyValueListTransformer())
        .create();
    this.conceptIndex = new ConceptIndex(em);
  }

  private <T> JsonObject toJson(T object) {
    return gson.toJsonTree(object).getAsJsonObject();
  }

  private <T> JsonArray toJson(List<T> object) {
    return gson.toJsonTree(object).getAsJsonArray();
  }

  private <T> T fromJson(JsonObject object, Class<T> cls) {
    return gson.fromJson(object, cls);
  }

  @Override
  public JsonElement saveConcept(JsonElement data) {
    if (data.isJsonObject()) {
      return saveConcept(data.getAsJsonObject());
    }
    if (data.isJsonArray()) {
      return saveConcept(data.getAsJsonArray());
    }
    return JsonNull.INSTANCE;
  }

  private JsonObject saveConcept(JsonObject concept) {
    return toJson(conceptIndex.index(
        conceptRepository.saveAndUpdateRelated(fromJson(concept, Concept.class))));
  }

  private JsonPrimitive saveConcept(JsonArray concepts) {
    int saved = 0;

    for (JsonElement element : concepts) {
      saveConcept(element.getAsJsonObject());
      saved++;
    }

    return new JsonPrimitive(saved);
  }

  @Override
  public JsonObject saveScheme(JsonObject data) {
    return toJson(schemeRepository.save(fromJson(data, Scheme.class)));
  }

  @Override
  public JsonObject saveCollection(JsonObject data) {
    return toJson(collectionRepository.save(fromJson(data, Collection.class)));
  }

  @Override
  public JsonObject getConcept(String id) {
    return conceptRepository.exists(id) ? toJson(conceptRepository.findOne(id)) : new JsonObject();
  }

  @Override
  public JsonObject getScheme(String id) {
    return schemeRepository.exists(id) ? toJson(schemeRepository.findOne(id)) : new JsonObject();
  }

  @Override
  public JsonObject getCollection(String id) {
    return collectionRepository.exists(id) ? toJson(collectionRepository.findOne(id))
                                           : new JsonObject();
  }

  @Override
  public JsonArray getConceptBroaderPaths(String id) {
    return conceptRepository.exists(id) ? gson
        .toJsonTree(ConceptUtils.findBroaderPaths(conceptRepository.findOne(id)))
        .getAsJsonArray() : new JsonArray();
  }

  @Override
  public JsonArray queryConcepts(String query, int first, int max, List<String> orderBy) {
    return gson.toJsonTree(conceptIndex.query(query, first, max, orderBy)).getAsJsonArray();
  }

  @Override
  public JsonArray queryConcepts(String schemeId, String query, int first, int max,
                                 List<String> orderBy) {
    return gson.toJsonTree(conceptIndex.query(
        addSchemeIdToQuery(schemeId, query), first, max, orderBy)).getAsJsonArray();
  }

  private String addSchemeIdToQuery(String schemeId, String query) {
    return LuceneQueryUtils.and(LuceneQueryUtils.termQuery("scheme.id", schemeId), query);
  }

  @Override
  public JsonArray querySchemes() {
    return toJson(schemeRepository.findAll());
  }

  @Override
  public JsonArray queryCollections(String schemeId) {
    return toJson(collectionRepository.findBySchemeId(schemeId));
  }

  @Override
  public void removeScheme(String id) {
    schemeRepository.delete(id);
  }

  @Override
  public void removeCollection(String id) {
    collectionRepository.delete(id);
  }

  @Override
  public void removeConcept(String id) {
    conceptRepository.deleteAndUpdateRelatedAndCollections(conceptRepository.findOne(id));
  }

}
