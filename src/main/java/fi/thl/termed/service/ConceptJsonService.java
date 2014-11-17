package fi.thl.termed.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.List;

public interface ConceptJsonService {

  JsonObject save(JsonObject concept);

  JsonPrimitive saveAll(JsonArray concepts);

  JsonObject get(String id);

  JsonArray query(String query, int first, int max, List<String> order);

  void remove(String id);

}
