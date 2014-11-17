package fi.thl.termed.repository;

import fi.thl.termed.model.Concept;

public interface ConceptRepositoryExtended {

  Concept saveAndUpdateRelated(Concept concept);

  void deleteAndUpdateRelated(Concept concept);

}
