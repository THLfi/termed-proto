package fi.thl.termed.service;

import com.google.common.collect.Lists;

import org.apache.lucene.search.Query;

import java.util.List;

import fi.thl.termed.domain.Concept;
import fi.thl.termed.domain.ConceptReference;

/**
 * Repository to handle complex Concept saving
 */
public class ConceptRepository implements Repository<Concept> {

  private HibernateSearchRepository<Concept> delegate;

  public ConceptRepository(HibernateSearchRepository<Concept> delegate) {
    this.delegate = delegate;
  }

  @Override
  public Concept save(Concept data) {
    return delegate.save(saveConceptReferences(data));
  }

  // workaround for saving references as they don't cascade easily with concept
  private Concept saveConceptReferences(Concept concept) {
    List<ConceptReference> references = Lists.newArrayList(concept.getReferences());

    // first save concept w/o refs
    concept.getReferences().clear();
    concept = delegate.getEntityManager().merge(concept);

    // merge refs separately
    List<ConceptReference> mergedReferences = Lists.newArrayList();
    for (ConceptReference reference : references) {
      reference.setSource(concept);
      mergedReferences.add(delegate.getEntityManager().merge(reference));
    }

    // restore merged refs and return concept for final saving and indexing
    concept.getReferences().addAll(mergedReferences);
    return concept;
  }

  @Override
  public Iterable<Concept> save(Iterable<Concept> data) {
    List<Concept> saved = Lists.newArrayList();

    for (Concept datum : data) {
      saved.add(save(datum));
    }

    return saved;
  }

  @Override
  public Concept get(String id) {
    return delegate.get(id);
  }

  @Override
  public List<Concept> query(Query query, int first, int max, List<String> orderBy) {
    return delegate.query(query, first, max, orderBy);
  }

  @Override
  public List<Concept> query(String query, int first, int max, List<String> orderBy) {
    return delegate.query(query, first, max, orderBy);
  }

  @Override
  public List<Concept> queryCached(String query, int first, int max, List<String> orderBy) {
    return delegate.queryCached(query, first, max, orderBy);
  }

  @Override
  public List<Concept> queryCached(Query query, int first, int max, List<String> orderBy) {
    return delegate.queryCached(query, first, max, orderBy);
  }

  @Override
  public void remove(String id) {
    delegate.remove(id);
  }

  @Override
  public int size() {
    return delegate.size();
  }

}
