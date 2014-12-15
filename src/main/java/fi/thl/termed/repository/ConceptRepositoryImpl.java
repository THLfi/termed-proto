package fi.thl.termed.repository;

import com.google.common.collect.Sets;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import fi.thl.termed.model.Collection;
import fi.thl.termed.model.Concept;

public class ConceptRepositoryImpl implements ConceptRepositoryExtended {

  @Autowired
  private ConceptRepository conceptRepository;

  @Autowired
  private CollectionRepository collectionRepository;

  @Override
  public Concept saveConcept(Concept concept) {
    updateRelated(concept);
    return conceptRepository.save(concept);
  }

  private void updateRelated(Concept concept) {
    Set<Concept> newRelated = Sets.newHashSet(getRelated(concept));
    Set<Concept> oldRelated = Sets.newHashSet(getRelated(concept.getId()));

    for (Concept removedFromRelated : Sets.difference(oldRelated, newRelated)) {
      removedFromRelated.removeRelated(concept);
    }
    for (Concept addedToRelated : Sets.difference(newRelated, oldRelated)) {
      addedToRelated.addRelated(concept);
    }

    conceptRepository.save(newRelated);
    conceptRepository.save(oldRelated);
  }

  private Concept getConcept(String conceptId) {
    return conceptId != null && conceptRepository.exists(conceptId) ?
           conceptRepository.findOne(conceptId) : null;
  }

  private List<Concept> getRelated(String conceptId) {
    return getRelated(getConcept(conceptId));
  }

  private List<Concept> getRelated(Concept concept) {
    return concept != null && concept.hasRelated() ? concept.getRelated()
                                                   : Collections.<Concept>emptyList();
  }

  @Override
  public void deleteConcept(Concept concept) {
    removeFromRelated(concept);
    removeFromCollections(concept);
    conceptRepository.delete(concept);
  }

  private void removeFromCollections(Concept concept) {
    if (concept.hasCollections()) {
      for (Collection collection : concept.getCollections()) {
        collection.removeMember(concept);
      }
      collectionRepository.save(concept.getCollections());
    }
  }

  private void removeFromRelated(Concept concept) {
    if (concept.hasRelated()) {
      for (Concept related : concept.getRelated()) {
        related.removeRelated(concept);
      }
      conceptRepository.save(concept.getRelated());
    }
  }

}
