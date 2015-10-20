package fi.thl.termed.service;

import com.google.gson.JsonArray;

import java.util.List;

public interface JsonConceptGraphService {

  JsonArray getConceptPaths(String conceptId, String referenceTypeId);

  JsonArray getConceptJsTrees(String conceptId, String referenceTypeId);

  JsonArray getConceptTrees(String schemeId, String referenceTypeId);

  JsonArray getConceptTrees(String schemeId, String referenceTypeId, List<String> orderBy);

}
