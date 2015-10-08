package fi.thl.termed.service;

import java.util.List;

import fi.thl.termed.model.Concept;
import fi.thl.termed.model.JsTree;
import fi.thl.termed.model.LazyConceptTree;

public interface ConceptTreeService {

  List<LazyConceptTree> getConceptNarrowerTree(Concept concept);

  List<JsTree> getConceptNarrowerJsTree(Concept concept);

}
