package fi.thl.termed.util;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

import fi.thl.termed.model.Concept;

public final class ConceptGraphUtils {

  private ConceptGraphUtils() {
  }

  public static final Function<Concept, List<Concept>> getBroaderFunction =
      new Function<Concept, List<Concept>>() {
        @Override
        public List<Concept> apply(Concept c) {
          return c.getBroader();
        }
      };

  public static final Function<Concept, List<Concept>> getNarrowerFunction =
      new Function<Concept, List<Concept>>() {
        @Override
        public List<Concept> apply(Concept c) {
          return c.getNarrower();
        }
      };

  public static final Function<Concept, List<Concept>> getTypesFunction =
      new Function<Concept, List<Concept>>() {
        @Override
        public List<Concept> apply(Concept c) {
          return c.getTypes();
        }
      };

  public static final Function<Concept, List<Concept>> getInstancesFunction =
      new Function<Concept, List<Concept>>() {
        @Override
        public List<Concept> apply(Concept c) {
          return c.getInstances();
        }
      };

  public static final Function<Concept, List<Concept>> getPartOfFunction =
      new Function<Concept, List<Concept>>() {
        @Override
        public List<Concept> apply(Concept c) {
          return c.getPartOf();
        }
      };

  public static final Function<Concept, List<Concept>> getPartsFunction =
      new Function<Concept, List<Concept>>() {
        @Override
        public List<Concept> apply(Concept c) {
          return c.getParts();
        }
      };

  public static final Function<Concept, List<Concept>> getRelatedFunction =
      new Function<Concept, List<Concept>>() {
        @Override
        public List<Concept> apply(Concept c) {
          return c.getRelated();
        }
      };

  public static final Function<Concept, List<Concept>> getRelatedFromFunction =
      new Function<Concept, List<Concept>>() {
        @Override
        public List<Concept> apply(Concept c) {
          return c.getRelatedFrom();
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

  /**
   * Pretty print concept graph as tree using neighbour function.
   */
  public static String prettyPrintTree(Concept concept,
                                       Function<Concept, List<Concept>> getNeighbours) {
    StringBuilder builder = new StringBuilder();
    prettyPrintTree("", concept, getNeighbours, builder);
    return builder.toString();
  }

  private static void prettyPrintTree(String indent, Concept concept,
                                      Function<Concept, List<Concept>> getNeighbours,
                                      StringBuilder builder) {
    builder.append(String.format("%s - %s\n", indent, concept.getId()));
    for (Concept neighbour : ListUtils.nullToEmpty(getNeighbours.apply(concept))) {
      prettyPrintTree(indent + "\t", neighbour, getNeighbours, builder);
    }
  }

  public static List<Concept> findRoots(List<List<Concept>> broaderPaths) {
    Set<Concept> roots = Sets.newHashSet();

    for (List<Concept> broaderPath : broaderPaths) {
      roots.add(broaderPath.get(0));
    }

    return Lists.newArrayList(roots);
  }


  /**
   * Collect all concepts reachable from root concepts using neighbour functions.
   */
  public static Set<Concept> collectConcepts(List<Concept> roots,
                                             List<Function<Concept, List<Concept>>> getNeighboursFunctions) {
    Set<Concept> results = Sets.newHashSet();

    for (Concept root : roots) {
      for (Function<Concept, List<Concept>> neighboursFunction : getNeighboursFunctions) {
        Set<Concept> r = Sets.newHashSet();
        collectConcepts(root, neighboursFunction, r);
        results.addAll(r);
      }
    }

    return results;
  }

  /**
   * Enumerate all concepts reachable from the concept. Flattening results from collectPaths yields
   * the same results as this but is slightly less efficient.
   */
  private static void collectConcepts(Concept concept,
                                      Function<Concept, List<Concept>> getNeighbours,
                                      Set<Concept> results) {
    if (!results.contains(concept)) {
      results.add(concept);
      for (Concept neighbour : ListUtils.nullToEmpty(getNeighbours.apply(concept))) {
        collectConcepts(neighbour, getNeighbours, results);
      }
    }
  }

  /**
   * Enumerate all paths starting from concept using getNeighbours function.
   */
  public static List<List<Concept>> collectPaths(Concept concept,
                                                 Function<Concept, List<Concept>> getNeighbours) {
    List<List<Concept>> paths = Lists.newArrayList();
    collectPaths(concept, getNeighbours, Sets.<Concept>newLinkedHashSet(), paths);
    return paths;
  }

  public static List<List<Concept>> collectBroaderPaths(Concept concept) {
    return collectPaths(concept, getBroaderFunction);
  }

  public static List<List<Concept>> collectPartOfPaths(Concept concept) {
    return collectPaths(concept, getPartOfFunction);
  }

  /**
   * Enumerate all paths from the concept
   */
  private static void collectPaths(Concept concept, Function<Concept, List<Concept>> getNeighbours,
                                   Set<Concept> path, List<List<Concept>> results) {

    if (!path.contains(concept)) {
      path.add(concept);
    } else {
      results.add(toReversedList(path));
      return;
    }

    List<Concept> neighbours = ListUtils.nullToEmpty(getNeighbours.apply(concept));

    if (!neighbours.isEmpty()) {
      for (Concept neighbour : neighbours) {
        collectPaths(neighbour, getNeighbours, Sets.newLinkedHashSet(path), results);
      }
    } else {
      results.add(toReversedList(path));
    }
  }

  private static List<Concept> toReversedList(Set<Concept> concepts) {
    return Lists.reverse(Lists.newArrayList(concepts));
  }

}
