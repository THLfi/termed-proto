package fi.thl.termed.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public interface JsonService {

  JsonObject saveConcept(JsonObject concept);

  JsonPrimitive saveAllConcepts(JsonArray concepts);

  JsonObject getConcept(String id);

  JsonArray queryConcepts();

  JsonArray queryConcepts(int max);

  JsonArray queryConcepts(String query, int max);

  void removeConcept(String id);

}
