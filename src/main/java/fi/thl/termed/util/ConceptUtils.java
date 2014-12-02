package fi.thl.termed.util;

import com.google.common.collect.Lists;

import java.util.List;

import fi.thl.termed.model.Concept;

public final class ConceptUtils {

  private ConceptUtils() {
  }

  public static List<List<Concept>> findBroaderPaths(Concept concept) {
    List<List<Concept>> paths = Lists.newArrayList();

    if (hasBroader(concept)) {
      for (Concept broader : concept.getBroader()) {
        findBroaderPaths(broader, Lists.newArrayList(concept), paths);
      }
    }

    return paths;
  }

  private static void findBroaderPaths(Concept concept, List<Concept> path,
                                       List<List<Concept>> paths) {
    path.add(concept);

    if (hasBroader(concept)) {
      for (Concept broader : concept.getBroader()) {
        findBroaderPaths(broader, Lists.newArrayList(path), paths);
      }
    } else {
      paths.add(Lists.reverse(path));
    }
  }

  private static boolean hasBroader(Concept concept) {
    return concept.getBroader() != null && !concept.getBroader().isEmpty();
  }

}
