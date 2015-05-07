package fi.thl.termed.util;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

import fi.thl.termed.model.Concept;
import fi.thl.termed.model.SchemeResource;

public final class ConceptGraphUtils {

  private ConceptGraphUtils() {
  }

  public static final Function<Concept, List<Concept>> getNarrowerFunction =
      new Function<Concept, List<Concept>>() {
        @Override
        public List<Concept> apply(Concept c) {
          return c.getNarrower();
        }
      };

  public static final Function<Concept, List<Concept>> getBroaderFunction =
      new Function<Concept, List<Concept>>() {
        @Override
        public List<Concept> apply(Concept c) {
          return c.getBroader();
        }
      };

  public static final Function<Concept, List<Concept>> getTypesFunction =
      new Function<Concept, List<Concept>>() {
        @Override
        public List<Concept> apply(Concept c) {
          return c.getTypes();
        }
      };

  public static final Function<Concept, List<Concept>> getPartOfFunction =
      new Function<Concept, List<Concept>>() {
        @Override
        public List<Concept> apply(Concept c) {
          return c.getPartOf();
        }
      };

  public static final Function<Concept, List<Concept>> getRelatedFunction =
      new Function<Concept, List<Concept>>() {
        @Override
        public List<Concept> apply(Concept c) {
          return c.getRelated();
        }
      };

  public static final List<Function<Concept, List<Concept>>> getNeighboursFunctions =
      Lists.newArrayList(getNarrowerFunction,
                         getBroaderFunction,
                         getTypesFunction,
                         getRelatedFunction);

  /**
   * Pretty print concept narrower graph as tree.
   */
  public static String prettyPrintTree(Concept concept) {
    StringBuilder builder = new StringBuilder();
    prettyPrintTree("", concept, builder);
    return builder.toString();
  }

  private static void prettyPrintTree(String indent, Concept concept, StringBuilder builder) {
    builder.append(String.format("%s - %s\n", indent, concept.getId()));
    if (concept.hasNarrower()) {
      for (Concept narrower : concept.getNarrower()) {
        prettyPrintTree(indent + "\t", narrower, builder);
      }
    }
  }

  /**
   * Find trees containing concept. Each tree is built from concept's broader path to root. Concepts
   * in the path are expanded (their narrower are included).
   *
   * @return roots of trees
   */
  public static List<Concept> broaderTrees(Concept concept) {
    List<List<Concept>> broaderPaths = collectBroaderPaths(concept);

    Set<Concept> accepted = Sets.newHashSet();
    for (Concept pathConcept : ListUtils.flatten(broaderPaths)) {
      accepted.add(pathConcept);
      if (pathConcept.hasNarrower()) {
        accepted.addAll(pathConcept.getNarrower());
      }
    }

    return copyTree(findRoots(broaderPaths), Predicates.in(accepted));
  }

  private static List<Concept> findRoots(List<List<Concept>> broaderPaths) {
    Set<Concept> roots = Sets.newHashSet();

    for (List<Concept> broaderPath : broaderPaths) {
      roots.add(broaderPath.get(0));
    }

    return Lists.newArrayList(roots);
  }

  public static List<Concept> copyTree(List<Concept> roots, Predicate<Concept> accepted) {
    return copyTree(null, roots, accepted);
  }

  private static List<Concept> copyTree(Concept broaderTreeConcept, List<Concept> concepts,
                                        Predicate<Concept> accepted) {

    List<Concept> treeConcepts = Lists.newArrayList();

    for (Concept concept : concepts) {
      if (accepted.apply(concept)) {
        treeConcepts.add(copyTree(broaderTreeConcept, concept, accepted));
      }
    }

    return treeConcepts;
  }

  private static Concept copyTree(Concept broaderTreeConcept, Concept concept,
                                  Predicate<Concept> accepted) {
    Concept treeConcept = new Concept(new SchemeResource(concept));
    if (broaderTreeConcept != null) {
      treeConcept.setBroader(Lists.newArrayList(broaderTreeConcept));
    }
    if (concept.hasNarrower()) {
      treeConcept.setNarrower(copyTree(treeConcept, concept.getNarrower(), accepted));
    }
    return treeConcept;
  }

  public static List<List<Concept>> collectBroaderPaths(Concept concept) {
    List<List<Concept>> paths = Lists.newArrayList();
    collectPaths(concept, getBroaderFunction, Sets.<Concept>newLinkedHashSet(), paths);
    return paths;
  }


  public static List<List<Concept>> collectPartOfPaths(Concept concept) {
    List<List<Concept>> paths = Lists.newArrayList();
    collectPaths(concept, getPartOfFunction, Sets.<Concept>newLinkedHashSet(), paths);
    return paths;
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
