package fi.thl.termed.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

import fi.thl.termed.model.Concept;

public final class ConceptUtils {

  private ConceptUtils() {
  }

  public static List<List<Concept>> findBroaderPaths(Concept concept) {
    List<List<Concept>> paths = Lists.newArrayList();

    if (hasBroader(concept)) {
      for (Concept broader : concept.getBroader()) {
        findBroaderPaths(broader, Sets.newLinkedHashSet(Lists.newArrayList(concept)), paths);
      }
    }

    return paths;
  }

  private static void findBroaderPaths(Concept concept, Set<Concept> path,
                                       List<List<Concept>> paths) {

    if (!path.contains(concept)) {
      path.add(concept);
    } else {
      paths.add(Lists.reverse(Lists.newArrayList(path)));
      return;
    }

    if (hasBroader(concept)) {
      for (Concept broader : concept.getBroader()) {
        findBroaderPaths(broader, Sets.newLinkedHashSet(path), paths);
      }
    } else {
      paths.add(Lists.reverse(Lists.newArrayList(path)));
    }
  }

  private static boolean hasBroader(Concept concept) {
    return concept.getBroader() != null && !concept.getBroader().isEmpty();
  }

}
