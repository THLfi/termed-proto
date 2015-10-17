package fi.thl.termed.service;

import com.google.gson.JsonArray;

public interface JsonConceptGraphService {

  JsonArray getConceptPaths(String conceptId, String referenceTypeId);

  JsonArray getConceptJsTrees(String conceptId, String referenceTypeId);

  JsonArray getConceptTrees(String schemeId, String referenceTypeId);

}
