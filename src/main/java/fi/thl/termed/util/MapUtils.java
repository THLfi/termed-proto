package fi.thl.termed.util;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;

import java.util.Map;

public final class MapUtils {

  private MapUtils() {
  }

  public static <L, R, V> Map<L, Map<R, V>> expandKeys(Map<Pair<L, R>, V> map) {
    Map<L, Map<R, V>> expanded = Maps.newHashMap();

    for (Map.Entry<Pair<L, R>, V> entry : map.entrySet()) {
      Pair<L, R> key = entry.getKey();

      if (!expanded.containsKey(key.getLeft())) {
        expanded.put(key.getLeft(), Maps.<R, V>newHashMap());
      }

      expanded.get(key.getLeft()).put(key.getRight(), entry.getValue());
    }

    return expanded;
  }

  public static <L, R, V> Map<Pair<L, R>, V> collapseKeys(Map<L, Map<R, V>> map) {
    Map<Pair<L, R>, V> collapsed = Maps.newHashMap();

    for (Map.Entry<L, Map<R, V>> entry : map.entrySet()) {
      L left = entry.getKey();
      Map<R, V> rightValueMap = entry.getValue();
      for (Map.Entry<R, V> rightValue : rightValueMap.entrySet()) {
        collapsed.put(new Pair<L, R>(left, rightValue.getKey()), rightValue.getValue());
      }
    }

    return collapsed;
  }

  public static <K, V> V get(Map<K, V> map, K key, Supplier<V> defaultValueSupplier) {
    if (!map.containsKey(key)) {
      map.put(key, defaultValueSupplier.get());
    }
    return map.get(key);
  }

  public static <K1, K2, V> Map<K2, V> getNestedMap(Map<K1, Map<K2, V>> map, K1 key) {
    return get(map, key, MapUtils.<K2, V>newHashMapSupplier());
  }

  public static <K, V> Supplier<Map<K, V>> newHashMapSupplier() {
    return new Supplier<Map<K, V>>() {
      @Override
      public Map<K, V> get() {
        return Maps.newHashMap();
      }
    };
  }

}
