package fi.thl.termed.repository;

import com.google.common.collect.Lists;

import org.springframework.stereotype.Repository;

import java.util.List;

import fi.thl.termed.domain.Concept;
import fi.thl.termed.domain.ConceptReference;

/**
 * Repository to handle complex Concept saving
 */
@Repository("concepts")
public class ConceptRepository extends HibernateSearchRepository<Concept> {

  @Override
  public Concept save(Concept data) {
    return super.save(saveConcept(data));
  }

  // workaround for saving references as they don't cascade easily with concept
  private Concept saveConcept(Concept concept) {
    if (concept.getReferences() == null) {
      return concept;
    }

    List<ConceptReference> references = Lists.newArrayList(concept.getReferences());

    // first save concept w/o refs
    concept.getReferences().clear();
    concept = getEntityManager().merge(concept);

    // merge refs separately
    List<ConceptReference> mergedReferences = Lists.newArrayList();
    for (ConceptReference reference : references) {
      reference.setSource(concept);
      mergedReferences.add(getEntityManager().merge(reference));
    }

    // restore merged refs and return concept for final saving and indexing
    concept.getReferences().addAll(mergedReferences);
    return concept;
  }

}
