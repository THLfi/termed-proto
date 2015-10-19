package fi.thl.termed.service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import org.apache.lucene.search.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import fi.thl.termed.domain.Concept;
import fi.thl.termed.domain.LazyConceptTree;
import fi.thl.termed.util.ConceptGraphUtils;
import fi.thl.termed.util.LuceneQueryBuilder;
import fi.thl.termed.util.LuceneQueryUtils;

@Service
@Transactional(readOnly = true)
public class ConceptGraphServiceImpl implements ConceptGraphService {

  @Autowired
  private CrudService crudService;

  private Function<Concept, List<Concept>> referencesFunction(final String referenceTypeId) {
    return new Function<Concept, List<Concept>>() {
      @Override
      public List<Concept> apply(Concept c) {
        return c.getReferencesByType(referenceTypeId);
      }
    };
  }

  private Function<Concept, List<Concept>> referrersFunction(final String referenceTypeId) {
    return new Function<Concept, List<Concept>>() {
      @Override
      public List<Concept> apply(Concept concept) {
        // for better performance, referrers are queried and loaded from from index
        return crudService.queryCached(Concept.class, LuceneQueryUtils.term(referenceTypeId + ".id",
                                                                            concept.getId()));
      }
    };
  }

  private Function<Concept, LazyConceptTree> conceptToLazyTreeFunction(
      final Function<Concept, List<Concept>> referencesFunction) {
    return new Function<Concept, LazyConceptTree>() {
      @Override
      public LazyConceptTree apply(Concept concept) {
        return new LazyConceptTree(concept, referencesFunction);
      }
    };
  }

  private Function<String, LazyConceptTree> conceptIdToLazyTreeFunction(
      final Function<Concept, List<Concept>> referencesFunction) {
    return new Function<String, LazyConceptTree>() {
      @Override
      public LazyConceptTree apply(String conceptId) {
        return new LazyConceptTree(crudService.get(Concept.class, conceptId), referencesFunction);
      }
    };
  }

  @Override
  public List<LazyConceptTree> roots(String schemeId, String referenceTypeId) {
    Query query = new LuceneQueryBuilder()
        .mustOccur().term("scheme.id", schemeId)
        .mustNotOccur().anyValueOfField(referenceTypeId + ".id").build();
    return Lists.transform(crudService.query(Concept.class, query),
                           conceptToLazyTreeFunction(referrersFunction(referenceTypeId)));
  }

  @Override
  public List<LazyConceptTree> toTrees(List<String> conceptIds, String referenceTypeId) {
    return Lists.transform(conceptIds,
                           conceptIdToLazyTreeFunction(referrersFunction(referenceTypeId)));
  }

  @Override
  public LazyConceptTree toTree(String conceptId, String referenceTypeId) {
    return conceptIdToLazyTreeFunction(referrersFunction(referenceTypeId)).apply(conceptId);
  }

  @Override
  public List<List<Concept>> conceptPaths(String conceptId, String referenceTypeId) {
    return ConceptGraphUtils.collectPaths(crudService.get(Concept.class, conceptId),
                                          referencesFunction(referenceTypeId));
  }

}
