package fi.thl.termed.util;

import com.google.common.collect.Lists;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import fi.thl.termed.model.Concept;

public class ConceptBroaderPathsFinderTest {

  @Test
  public void shouldFindSimpleParentPath() {
    Concept root = new Concept("root");
    Concept branch = new Concept("branch");
    Concept leaf = new Concept("leaf");

    branch.setBroader(Lists.newArrayList(root));
    leaf.setBroader(Lists.newArrayList(branch));

    List<List<Concept>> expectedPaths = Lists.newArrayList();
    expectedPaths.add(Lists.newArrayList(root, branch, leaf));

    Assert.assertEquals(expectedPaths, ConceptUtils.findBroaderPaths(leaf));
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

    Assert.assertEquals(expectedPaths, ConceptUtils.findBroaderPaths(leaf));
  }

}
