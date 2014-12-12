package fi.thl.termed.util;

import com.google.common.collect.Lists;

import java.util.List;

public final class ListUtils {

  private ListUtils() {
  }

  public static <T> List<T> flatten(List<List<T>> listOfLists) {
    List<T> result = Lists.newArrayList();

    for (List<T> list : listOfLists) {
      for (T value : list) {
        result.add(value);
      }
    }

    return result;
  }

}
