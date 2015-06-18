package fi.thl.termed.util;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import fi.thl.termed.model.Concept;
import fi.thl.termed.model.ConceptReference;

public final class ConceptReferenceFunctions {

  private ConceptReferenceFunctions() {
  }

  public static Function<Concept, List<Concept>> buildReferenceFunctionByType(final String referenceTypeId) {
    return new Function<Concept, List<Concept>>() {
      @Override
      public List<Concept> apply(Concept c) {
        return c.getReferencesByType(referenceTypeId);
      }
    };
  }

  public static Function<Concept, List<Concept>> buildReferrerFunctionByType(final String referenceTypeId) {
    return new Function<Concept, List<Concept>>() {
      @Override
      public List<Concept> apply(Concept c) {
        return c.getReferrersByType(referenceTypeId);
      }
    };
  }

  public static final Function<Concept, List<Concept>> getBroaderFunction =
      buildReferenceFunctionByType("broader");
  public static final Function<Concept, List<Concept>> getNarrowerFunction =
      buildReferrerFunctionByType("broader");
  public static final Function<Concept, List<Concept>> getPartOfFunction =
      buildReferenceFunctionByType("partOf");
  public static final Function<Concept,List<Concept>> getPartsFunction =
      buildReferrerFunctionByType("partOf");
  public static final Function<Concept, List<Concept>> getTypesFunction =
      buildReferenceFunctionByType("type");
  public static final Function<Concept,List<Concept>> getInstancesFunction =
      buildReferrerFunctionByType("type");

  public static final Function<Concept, List<Concept>> getReferencesFunction =
      new Function<Concept, List<Concept>>() {
        @Override
        public List<Concept> apply(Concept c) {
          List<Concept> results = Lists.newArrayList();

          for (ConceptReference reference : ListUtils.nullToEmpty(c.getReferences())) {
              results.add(reference.getTarget());
          }

          return results;
        }
      };

  public static final Function<Concept, List<Concept>> getReferrersFunction =
      new Function<Concept, List<Concept>>() {
        @Override
        public List<Concept> apply(Concept c) {
          List<Concept> results = Lists.newArrayList();

          for (ConceptReference referrer : ListUtils.nullToEmpty(c.getReferrers())) {
              results.add(referrer.getSource());
          }

          return results;
        }
      };

}
