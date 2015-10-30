package fi.thl.termed.util;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

public final class ListUtils {

  private ListUtils() {
  }

  public static <T> List<T> concat(List<? extends T> l1, List<? extends T> l2) {
    return Lists.newArrayList(Iterables.concat(l1, l2));
  }

  public static <T> List<T> filter(List<T> list, Predicate<? super T> predicate) {
    return Lists.newArrayList(Iterables.filter(list, predicate));
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

  public static <T> List<T> nullToEmpty(List<T> list) {
    return list == null ? Collections.<T>emptyList() : list;
  }

  public static <T> boolean isNullOrEmpty(List<T> list) {
    return list == null || list.isEmpty();
  }

}
