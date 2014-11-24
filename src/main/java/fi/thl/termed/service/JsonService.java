package fi.thl.termed.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

public interface JsonService {

  // scheme

  JsonObject saveScheme(JsonObject data);

  JsonObject getScheme(String id);

  JsonArray querySchemes();

  void removeScheme(String id);

  // concept

  JsonElement saveConcept(JsonElement data);

  JsonObject getConcept(String id);

  JsonArray queryConcepts(String query, int first, int max, List<String> orderBy);

  JsonArray queryConcepts(String schemeId, String query, int first, int max, List<String> orderBy);

  void removeConcept(String id);

  // collection

  JsonObject saveCollection(JsonObject data);

  JsonObject getCollection(String id);

  JsonArray queryCollections(String schemeId);

  void removeCollection(String id);

}
