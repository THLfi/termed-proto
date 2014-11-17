package fi.thl.termed.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import fi.thl.termed.model.Concept;
import fi.thl.termed.repository.ConceptRepository;
import fi.thl.termed.util.ConceptTransformer;
import fi.thl.termed.util.HibernateProxyTypeAdapterFactory;
import fi.thl.termed.util.PropertyValueListTransformer;

@Service
@Transactional
public class JsonServiceImpl implements JsonService {

  private ConceptRepository conceptRepository;
  private Gson gson;

  @PersistenceContext
  private EntityManager em;

  @Autowired
  public JsonServiceImpl(ConceptRepository conceptRepository) {
    this.conceptRepository = conceptRepository;
    // init post construct to get entity manager
    this.gson = null;
  }

  @PostConstruct
  public void initGson() {
    this.gson = new GsonBuilder().setPrettyPrinting()
        .registerTypeAdapter(Concept.class, new ConceptTransformer(em))
        .registerTypeAdapterFactory(new HibernateProxyTypeAdapterFactory())
        .registerTypeAdapter(PropertyValueListTransformer.PROPERTY_LIST_TYPE,
                             new PropertyValueListTransformer())
        .create();
  }

  @Override
  public JsonObject saveConcept(JsonObject concept) {
    return gson.toJsonTree(conceptRepository.saveAndUpdateRelated(
        gson.fromJson(concept, Concept.class))).getAsJsonObject();
  }

  @Override
  public JsonPrimitive saveAllConcepts(JsonArray concepts) {
    int saved = 0;

    for (JsonElement element : concepts) {
      saveConcept(element.getAsJsonObject());
      saved++;
    }

    return new JsonPrimitive(saved);
  }

  @Override
  public JsonObject getConcept(String id) {
    return conceptRepository.exists(id) ?
           gson.toJsonTree(conceptRepository.findOne(id)).getAsJsonObject() : new JsonObject();
  }

  @Override
  public JsonArray queryConcepts() {
    return gson.toJsonTree(conceptRepository.findAll()).getAsJsonArray();
  }

  @Override
  public JsonArray queryConcepts(int max) {
    return gson.toJsonTree(conceptRepository.findAll(new PageRequest(0, max)).getContent())
        .getAsJsonArray();
  }

  @Override
  public JsonArray queryConcepts(String query, int max) {
    return queryConcepts(max);
  }

  @Override
  public void removeConcept(String id) {
    conceptRepository.delete(id);
  }

}
