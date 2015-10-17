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

  /**
   * Pretty print concept graph as tree using neighbour function.
   */
  public static String prettyPrintTree(Concept concept,
                                       Function<Concept, List<Concept>> neighbourFunction) {
    StringBuilder builder = new StringBuilder();
    prettyPrintTree("", concept, neighbourFunction, builder);
    return builder.toString();
  }

  private static void prettyPrintTree(String indent, Concept concept,
                                      Function<Concept, List<Concept>> neighbourFunction,
                                      StringBuilder builder) {
    builder.append(String.format("%s - %s\n", indent, concept.getId()));
    for (Concept neighbour : ListUtils.nullToEmpty(neighbourFunction.apply(concept))) {
      prettyPrintTree(indent + "\t", neighbour, neighbourFunction, builder);
    }
  }


  public static List<Concept> findRoots(Concept concept,
                                        Function<Concept, List<Concept>> neighbourFunction) {
    return findRoots(collectPaths(concept, neighbourFunction));

  }

  public static List<Concept> findRoots(List<List<Concept>> paths) {
    Set<Concept> roots = Sets.newHashSet();

    for (List<Concept> path : paths) {
      roots.add(path.get(0));
    }

    return Lists.newArrayList(roots);
  }


  /**
   * Collect all concepts reachable from root concepts using neighbour functions.
   */
  public static Set<Concept> collectConcepts(List<Concept> roots,
                                             Function<Concept, List<Concept>> neighbourFunctionFunction) {
    Set<Concept> results = Sets.newHashSet();

    for (Concept root : roots) {
      Set<Concept> r = Sets.newHashSet();
      collectConcepts(root, neighbourFunctionFunction, r);
      results.addAll(r);
    }

    return results;
  }

  /**
   * Enumerate all concepts reachable from the concept. Flattening results from collectPaths yields
   * the same results as this but is slightly less efficient.
   */
  private static void collectConcepts(Concept concept,
                                      Function<Concept, List<Concept>> neighbourFunction,
                                      Set<Concept> results) {
    if (!results.contains(concept)) {
      results.add(concept);
      for (Concept neighbour : ListUtils.nullToEmpty(neighbourFunction.apply(concept))) {
        collectConcepts(neighbour, neighbourFunction, results);
      }
    }
  }

  /**
   * Enumerate all paths starting from concept using neighbourFunction function.
   */
  public static List<List<Concept>> collectPaths(Concept concept,
                                                 Function<Concept, List<Concept>> neighbourFunction) {
    List<List<Concept>> paths = Lists.newArrayList();
    collectPaths(concept, neighbourFunction, Sets.<Concept>newLinkedHashSet(), paths);
    return paths;
  }

  /**
   * Enumerate all paths from the concept
   */
  private static void collectPaths(Concept concept,
                                   Function<Concept, List<Concept>> neighbourFunction,
                                   Set<Concept> path,
                                   List<List<Concept>> results) {

    if (!path.contains(concept)) {
      path.add(concept);
    } else {
      results.add(toReversedList(path));
      return;
    }

    List<Concept> neighbours = ListUtils.nullToEmpty(neighbourFunction.apply(concept));

    if (!neighbours.isEmpty()) {
      for (Concept neighbour : neighbours) {
        collectPaths(neighbour, neighbourFunction, Sets.newLinkedHashSet(path), results);
      }
    } else {
      results.add(toReversedList(path));
    }
  }

  private static List<Concept> toReversedList(Set<Concept> concepts) {
    return Lists.reverse(Lists.newArrayList(concepts));
  }

}
