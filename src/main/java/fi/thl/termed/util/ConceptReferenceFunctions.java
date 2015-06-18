package fi.thl.termed.util;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

import fi.thl.termed.model.Concept;

public final class ConceptReferenceFunctions {

  private ConceptReferenceFunctions() {
  }


  public static final Function<Concept, List<Concept>> getBroaderFunction =
      new Function<Concept, List<Concept>>() {
        @Override
        public List<Concept> apply(Concept c) {
          return c.getReferencesByType("broader");
        }
      };

  public static final Function<Concept, List<Concept>> getNarrowerFunction =
      new Function<Concept, List<Concept>>() {
        @Override
        public List<Concept> apply(Concept c) {
          return c.getReferrersByType("broader");
        }
      };

  public static final Function<Concept, List<Concept>> getTypesFunction =
      new Function<Concept, List<Concept>>() {
        @Override
        public List<Concept> apply(Concept c) {
          return c.getReferencesByType("type");
        }
      };

  public static final Function<Concept, List<Concept>> getInstancesFunction =
      new Function<Concept, List<Concept>>() {
        @Override
        public List<Concept> apply(Concept c) {
          return c.getReferrersByType("type");
        }
      };

  public static final Function<Concept, List<Concept>> getPartOfFunction =
      new Function<Concept, List<Concept>>() {
        @Override
        public List<Concept> apply(Concept c) {
          return c.getReferencesByType("partOf");
        }
      };

  public static final Function<Concept, List<Concept>> getPartsFunction =
      new Function<Concept, List<Concept>>() {
        @Override
        public List<Concept> apply(Concept c) {
          return c.getReferrersByType("partOf");
        }
      };

  public static final Function<Concept, List<Concept>> getRelatedFunction =
      new Function<Concept, List<Concept>>() {
        @Override
        public List<Concept> apply(Concept c) {
          return c.getReferencesByType("related");
        }
      };

  public static final Function<Concept, List<Concept>> getRelatedFromFunction =
      new Function<Concept, List<Concept>>() {
        @Override
        public List<Concept> apply(Concept c) {
          return c.getReferrersByType("related");
        }
      };

  @SuppressWarnings("unchecked")
  public static final List<Function<Concept, List<Concept>>> getNeighboursFunctions =
      Lists.newArrayList(getBroaderFunction,
                         getNarrowerFunction,
                         getTypesFunction,
                         getInstancesFunction,
                         getPartOfFunction,
                         getPartsFunction,
                         getRelatedFunction,
                         getRelatedFromFunction);

  public static final Function<Concept, List<Concept>> getAllNeighboursFunction =
      new Function<Concept, List<Concept>>() {
        @Override
        public List<Concept> apply(Concept c) {
          Set<Concept> neighbours = Sets.newLinkedHashSet();

          for (Function<Concept, List<Concept>> f : getNeighboursFunctions) {
            neighbours.addAll(ListUtils.nullToEmpty(f.apply(c)));
          }

          return Lists.newArrayList(neighbours);
        }
      };

}
