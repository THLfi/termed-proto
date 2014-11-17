package fi.thl.termed.util;

import com.google.common.collect.Sets;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

public final class MapUtils {

  private MapUtils() {
  }

  public static <K, V> Set<Map.Entry<K, V>> entrySet(Map<K, Set<V>> map) {
    Set<Map.Entry<K, V>> entrySet = Sets.newHashSet();

    for (Map.Entry<K, Set<V>> entry : map.entrySet()) {
      for (V value : entry.getValue()) {
        entrySet.add(new AbstractMap.SimpleEntry<K, V>(entry.getKey(), value));
      }
    }

    return entrySet;
  }

}
