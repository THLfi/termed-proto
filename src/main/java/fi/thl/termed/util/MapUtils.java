package fi.thl.termed.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.Map;
import java.util.Set;

public final class MapUtils {

  private MapUtils() {
  }

  public static <K, V> Multimap<K, V> newHashMultimap(Map<K, Set<V>> map) {
    Multimap<K, V> multimap = HashMultimap.create();

    for (Map.Entry<K, Set<V>> entry : map.entrySet()) {
      for (V value : entry.getValue()) {
        multimap.put(entry.getKey(), value);
      }
    }

    return multimap;
  }

}
