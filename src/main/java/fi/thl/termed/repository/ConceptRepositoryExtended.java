package fi.thl.termed.repository;

import fi.thl.termed.model.Concept;

public interface ConceptRepositoryExtended {

  Concept saveConcept(Concept concept);

  void deleteConcept(Concept concept);

}
