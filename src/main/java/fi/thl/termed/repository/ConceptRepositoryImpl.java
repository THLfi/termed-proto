package fi.thl.termed.repository;

import org.springframework.beans.factory.annotation.Autowired;

import fi.thl.termed.model.Concept;

public class ConceptRepositoryImpl implements ConceptRepositoryExtended {

  @Autowired
  private ConceptRepository conceptRepository;

  @Override
  public Concept saveAndUpdateRelated(Concept concept) {
    addToRelated(concept);
    return conceptRepository.save(concept);
  }

  private void addToRelated(Concept concept) {
    if (concept.getRelated() != null) {
      for (Concept related : concept.getRelated()) {
        related.addRelated(concept);
      }
      conceptRepository.save(concept.getRelated());
    }
  }

  @Override
  public void deleteAndUpdateRelated(Concept concept) {
    removeFromRelated(concept);
    conceptRepository.delete(concept);
  }

  private void removeFromRelated(Concept concept) {
    if (concept.getRelated() != null) {
      for (Concept related : concept.getRelated()) {
        related.removeRelated(concept);
      }
      conceptRepository.save(concept.getRelated());
    }
  }

}
