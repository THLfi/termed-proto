package fi.thl.termed.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

public interface JsonCrudService {

  JsonObject save(String collection, JsonObject data);

  JsonObject save(String collection, JsonObject data, Gson gson);

  JsonArray save(String collection, JsonArray data);

  JsonArray save(String collection, JsonArray data, Gson gson);

  JsonObject get(String collection, String id);

  JsonObject get(String collection, String id, Gson gson);

  JsonArray query(String collection, String query);

  JsonArray query(String collection, String query, Gson gson);

  JsonArray query(String collection, String query, int first, int max, List<String> orderBy);

  JsonArray query(String collection, String query, int first, int max, List<String> orderBy,
                  Gson gson);

  void remove(String collection, String id);

}
