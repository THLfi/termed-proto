package fi.thl.termed.util;

import com.google.common.collect.Lists;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ListUtilsTest {

  @Test
  public void shouldFlattenNestedLists() {
    List<List<String>> listOfLists = Lists.newArrayList();
    listOfLists.add(Lists.newArrayList("a", "b"));
    listOfLists.add(Lists.newArrayList("c", "d"));

    assertEquals(Lists.newArrayList("a", "b", "c", "d"), ListUtils.flatten(listOfLists));
  }

}