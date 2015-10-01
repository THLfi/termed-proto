package fi.thl.termed.service;

import com.google.gson.JsonArray;

public interface JsonConceptGraphService {

  JsonArray getConceptBroaderPaths(String id);

  JsonArray getConceptPartOfPaths(String id);

  JsonArray getConceptJsTrees(String id);

}
