package fi.thl.termed.repository;

import org.springframework.beans.factory.annotation.Autowired;

import fi.thl.termed.model.Collection;
import fi.thl.termed.model.Concept;

public class ConceptRepositoryImpl implements ConceptRepositoryExtended {

  @Autowired
  private ConceptRepository conceptRepository;

  @Autowired
  private CollectionRepository collectionRepository;

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
  public void deleteAndUpdateRelatedAndCollections(Concept concept) {
    removeFromRelated(concept);
    removeFromCollections(concept);
    conceptRepository.delete(concept);
  }

  private void removeFromCollections(Concept concept) {
    if (concept.getCollections() != null) {
      for (Collection collection : concept.getCollections()) {
        collection.removeMember(concept);
      }
      collectionRepository.save(concept.getCollections());
    }
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
