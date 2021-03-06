package fi.thl.termed.util;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import org.junit.Test;

import java.util.List;

import fi.thl.termed.domain.Concept;
import fi.thl.termed.domain.ReferenceType;

import static org.junit.Assert.assertEquals;

public class ConceptGraphUtilsTest {

  private Function<Concept, List<Concept>> narrowerFunction =
      new Function<Concept, List<Concept>>() {
        @Override
        public List<Concept> apply(Concept c) {
          return c.getReferrersByType("broader");
        }
      };

  private Function<Concept, List<Concept>> broaderFunction =
      new Function<Concept, List<Concept>>() {
        @Override
        public List<Concept> apply(Concept c) {
          return c.getReferencesByType("broader");
        }
      };


  @Test
  public void shouldPrintNarrowerTree() {
    ReferenceType broader = new ReferenceType("broader");

    Concept root = new Concept("root");
    Concept branch1 = new Concept("branch1");
    Concept branch2 = new Concept("branch2");
    Concept branch3 = new Concept("branch3");
    Concept leaf1 = new Concept("leaf1");
    Concept leaf2 = new Concept("leaf2");
    Concept leaf3 = new Concept("leaf3");
    Concept leaf4 = new Concept("leaf4");

    root.addReferrers(broader, branch1, branch2, branch3);

    branch1.addReferences(broader, root);
    branch1.addReferrers(broader, leaf1, leaf2);

    branch2.addReferences(broader, root);

    branch3.addReferences(broader, root);
    branch3.addReferrers(broader, leaf3, leaf4);

    leaf1.addReferences(broader, branch1);
    leaf1.addReferrers(broader, leaf2);

    leaf2.addReferences(broader, leaf1, branch1);

    leaf3.addReferences(broader, branch3);
    leaf4.addReferences(broader, branch3);

    String exampleGraph = " - root\n"
                          + "\t - branch1\n"
                          + "\t\t - leaf1\n"
                          + "\t\t\t - leaf2\n"
                          + "\t\t - leaf2\n"
                          + "\t - branch2\n"
                          + "\t - branch3\n"
                          + "\t\t - leaf3\n"
                          + "\t\t - leaf4\n";

    assertEquals(exampleGraph, ConceptGraphUtils
        .prettyPrintTree(root, narrowerFunction));
  }

  @Test
  public void shouldFindSimpleParentPath() {
    ReferenceType broader = new ReferenceType("broader");

    Concept root = new Concept("root");
    Concept branch = new Concept("branch");
    Concept leaf = new Concept("leaf");

    branch.addReferences(broader, root);
    leaf.addReferences(broader, branch);

    List<List<Concept>> expectedPaths = Lists.newArrayList();
    expectedPaths.add(Lists.newArrayList(root, branch, leaf));

    assertEquals(expectedPaths, ConceptGraphUtils.collectPaths(leaf, broaderFunction));
  }

  @Test
  public void shouldFindDiamondParentPaths() {
    ReferenceType broader = new ReferenceType("broader");

    Concept root = new Concept("root");
    Concept branch1 = new Concept("branch1");
    Concept branch2 = new Concept("branch2");
    Concept leaf = new Concept("leaf");

    branch1.addReferences(broader, root);
    branch2.addReferences(broader, root);
    leaf.addReferences(broader, branch1, branch2);

    List<List<Concept>> expectedPaths = Lists.newArrayList();
    expectedPaths.add(Lists.newArrayList(root, branch1, leaf));
    expectedPaths.add(Lists.newArrayList(root, branch2, leaf));

    assertEquals(expectedPaths, ConceptGraphUtils.collectPaths(leaf, broaderFunction));
  }

}
