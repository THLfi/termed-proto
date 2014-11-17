package fi.thl.termed.util;

import com.google.common.base.Supplier;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.Map;

public final class MapUtils {

  private MapUtils() {
  }

  public static <K, V> Multimap<K, V> newHashMultimap(Map<K, Collection<V>> map) {
    Multimap<K, V> multimap = HashMultimap.create();

    for (Map.Entry<K, Collection<V>> entry : map.entrySet()) {
      for (V value : entry.getValue()) {
        multimap.put(entry.getKey(), value);
      }
    }

    return multimap;
  }

}
