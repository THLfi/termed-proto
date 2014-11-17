package fi.thl.termed.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public interface ConceptJsonService {

  JsonObject save(JsonObject concept);

  JsonPrimitive saveAll(JsonArray concepts);

  JsonObject get(String id);

  JsonArray query();

  JsonArray query(int max);

  JsonArray query(String query, int max);

  void remove(String id);

}
