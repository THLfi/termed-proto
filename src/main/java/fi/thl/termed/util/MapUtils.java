package fi.thl.termed.util;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class MapUtils {

  private MapUtils() {
  }

  public static <K, V> Map<K, List<V>> put(Map<K, List<V>> map, K key, V val) {
    if (!map.containsKey(key)) {
      map.put(key, Lists.<V>newArrayList());
    }

    map.get(key).add(val);

    return map;
  }

  public static <K, V> Map<K, V> nullToEmpty(Map<K, V> map) {
    return map == null ? Collections.<K, V>emptyMap() : map;
  }

}
