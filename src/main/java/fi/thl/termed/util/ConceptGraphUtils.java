package fi.thl.termed.util;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

import fi.thl.termed.model.Concept;
import fi.thl.termed.model.SchemeResource;

public final class ConceptGraphUtils {

  public static void main(String[] args) {
    Concept root = new Concept("root");
    Concept branch1 = new Concept("branch1");
    Concept branch2 = new Concept("branch2");
    Concept branch3 = new Concept("branch3");
    Concept leaf1 = new Concept("leaf1");
    Concept leaf2 = new Concept("leaf2");
    Concept leaf3 = new Concept("leaf3");
    Concept leaf4 = new Concept("leaf4");

    root.setNarrower(Lists.newArrayList(branch1, branch2, branch3));

    branch1.setBroader(Lists.newArrayList(root));
    branch1.setNarrower(Lists.newArrayList(leaf1, leaf2));

    branch2.setBroader(Lists.newArrayList(root));

    branch3.setBroader(Lists.newArrayList(root));
    branch3.setNarrower(Lists.newArrayList(leaf3, leaf4));

    leaf1.setBroader(Lists.newArrayList(branch1));
    leaf1.setNarrower(Lists.newArrayList(leaf2));

    leaf2.setBroader(Lists.newArrayList(leaf1, branch1));
    leaf3.setBroader(Lists.newArrayList(branch3));
    leaf4.setBroader(Lists.newArrayList(branch3));

    System.out.println(prettyPrintTree(root));

    for (Concept tree : broaderTrees(leaf1)) {
      System.out.println(prettyPrintTree(tree));
    }

    for (Concept tree : broaderTrees(leaf4)) {
      System.out.println(prettyPrintTree(tree));
    }
  }

  private ConceptGraphUtils() {
  }

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

  public static List<Concept> broaderTrees(Concept concept) {
    List<List<Concept>> broaderPaths = findBroaderPaths(concept);

    Set<Concept> accepted = Sets.newHashSet();
    for (Concept pathConcept : ListUtils.flatten(broaderPaths)) {
      accepted.add(pathConcept);
      if (pathConcept.hasNarrower()) {
        accepted.addAll(pathConcept.getNarrower());
      }
    }

    return copyTree(findRoots(broaderPaths), Predicates.in(accepted));
  }

  private static List<Concept> copyTree(List<Concept> roots, Predicate<Concept> accepted) {
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

  private static List<Concept> findRoots(List<List<Concept>> broaderPaths) {
    Set<Concept> roots = Sets.newHashSet();

    for (List<Concept> broaderPath : broaderPaths) {
      roots.add(broaderPath.get(0));
    }

    return Lists.newArrayList(roots);
  }

  public static List<List<Concept>> findBroaderPaths(Concept concept) {
    List<List<Concept>> paths = Lists.newArrayList();
    findBroaderPaths(concept, Lists.newArrayList(concept), paths);
    return paths;
  }

  private static void findBroaderPaths(Concept concept, List<Concept> path,
                                       List<List<Concept>> paths) {

    path.add(concept);

    if (concept.hasBroader()) {
      for (Concept broader : concept.getBroader()) {
        findBroaderPaths(broader, Lists.newArrayList(path), paths);
      }
    } else {
      paths.add(Lists.reverse(path));
    }
  }

}
