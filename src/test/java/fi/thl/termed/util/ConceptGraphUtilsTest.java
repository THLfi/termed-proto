package fi.thl.termed.util;

import com.google.common.collect.Lists;

import org.junit.Test;

import java.util.List;

import fi.thl.termed.model.Concept;

import static org.junit.Assert.assertEquals;

public class ConceptGraphUtilsTest {

  @Test
  public void shouldFindBroaderTree() {
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

    String exampleGraph = " - root\n"
                          + "\t - branch1\n"
                          + "\t\t - leaf1\n"
                          + "\t\t\t - leaf2\n"
                          + "\t\t - leaf2\n"
                          + "\t - branch2\n"
                          + "\t - branch3\n"
                          + "\t\t - leaf3\n"
                          + "\t\t - leaf4\n";
    assertEquals(exampleGraph, ConceptGraphUtils.prettyPrintTree(root));

    String broaderTreeForLeaf1 = " - root\n"
                                 + "\t - branch1\n"
                                 + "\t\t - leaf1\n"
                                 + "\t\t\t - leaf2\n"
                                 + "\t\t - leaf2\n"
                                 + "\t - branch2\n"
                                 + "\t - branch3\n";
    for (Concept tree : ConceptGraphUtils.broaderTrees(leaf1)) {
      assertEquals(broaderTreeForLeaf1, ConceptGraphUtils.prettyPrintTree(tree));
    }

    String broaderTreeForLeaf4 = " - root\n"
                                 + "\t - branch1\n"
                                 + "\t - branch2\n"
                                 + "\t - branch3\n"
                                 + "\t\t - leaf3\n"
                                 + "\t\t - leaf4\n";
    for (Concept tree : ConceptGraphUtils.broaderTrees(leaf4)) {
      assertEquals(broaderTreeForLeaf4, ConceptGraphUtils.prettyPrintTree(tree));
    }
  }

  @Test
  public void shouldFindSimpleParentPath() {
    Concept root = new Concept("root");
    Concept branch = new Concept("branch");
    Concept leaf = new Concept("leaf");

    branch.setBroader(Lists.newArrayList(root));
    leaf.setBroader(Lists.newArrayList(branch));

    List<List<Concept>> expectedPaths = Lists.newArrayList();
    expectedPaths.add(Lists.newArrayList(root, branch, leaf));

    assertEquals(expectedPaths, ConceptGraphUtils.findBroaderPaths(leaf));
  }

  @Test
  public void shouldFindDiamondParentPaths() {
    Concept root = new Concept("root");
    Concept branch1 = new Concept("branch1");
    Concept branch2 = new Concept("branch2");
    Concept leaf = new Concept("leaf");

    branch1.setBroader(Lists.newArrayList(root));
    branch2.setBroader(Lists.newArrayList(root));
    leaf.setBroader(Lists.newArrayList(branch1, branch2));

    List<List<Concept>> expectedPaths = Lists.newArrayList();
    expectedPaths.add(Lists.newArrayList(root, branch1, leaf));
    expectedPaths.add(Lists.newArrayList(root, branch2, leaf));

    assertEquals(expectedPaths, ConceptGraphUtils.findBroaderPaths(leaf));
  }

}
