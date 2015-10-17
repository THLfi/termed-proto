package fi.thl.termed.service;

import java.util.List;

import fi.thl.termed.model.Concept;
import fi.thl.termed.model.LazyConceptTree;
import fi.thl.termed.model.Scheme;

public interface ConceptGraphService {

  List<LazyConceptTree> roots(String schemeId, String referenceTypeId);

  LazyConceptTree toTree(String conceptId, String referenceTypeId);

  List<LazyConceptTree> toTrees(List<String> conceptIds, String referenceTypeId);

  List<List<Concept>> conceptPaths(String conceptId, String referenceTypeId);

}
