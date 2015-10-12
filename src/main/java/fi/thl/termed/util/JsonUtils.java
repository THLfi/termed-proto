package fi.thl.termed.util;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JsonUtils {

  private static final Pattern PROPERTY_PATH_PATTERN =
      Pattern.compile("\\.?([^.\\[\\]]+)|\\[(\\d+)\\]");

  private JsonUtils() {
  }

  public static JsonElement filter(JsonElement element, Predicate<JsonElement>... predicates) {
    JsonElement result = element;
    for (Predicate<JsonElement> predicate : predicates) {
      result = filter(result, predicate);
    }
    return result;
  }

  public static JsonElement filter(JsonElement element, Predicate<JsonElement> predicate) {
    if (element.isJsonObject()) {
      return filter(element.getAsJsonObject(), new JsonObject(), predicate);
    } else if (element.isJsonArray()) {
      return filter(element.getAsJsonArray(), new JsonArray(), predicate);
    }
    return element;
  }

  private static JsonElement filter(JsonObject source, JsonObject target,
                                    Predicate<JsonElement> predicate) {
    for (Map.Entry<String, JsonElement> entry : source.entrySet()) {
      JsonElement filterValue = filter(entry.getValue(), predicate);
      if (predicate.apply(filterValue)) {
        target.add(entry.getKey(), filterValue);
      }
    }
    return target;
  }

  private static JsonElement filter(JsonArray source, JsonArray target,
                                    Predicate<JsonElement> predicate) {
    for (JsonElement element : source) {
      JsonElement filteredElement = filter(element, predicate);
      if (predicate.apply(filteredElement)) {
        target.add(filteredElement);
      }
    }
    return target;
  }

  /**
   * Create map from json element where nested structures are flattened to multi part keys separated
   * with dot. E.g. "{"foo":{"bar":"value"}}" is flattened to "foo.bar": "value".
   *
   * @param element to be flattened
   * @return map containing flattened json element
   */
  public static Map<String, String> flatten(JsonElement element) {
    Map<String, String> results = Maps.newHashMap();
    flatten(element, new ArrayDeque<String>(), results);
    return results;
  }

  private static void flatten(JsonElement element, Deque<String> path, Map<String, String> map) {
    if (element.isJsonObject()) {
      flatten(element.getAsJsonObject(), path, map);
    } else if (element.isJsonArray()) {
      flatten(element.getAsJsonArray(), path, map);
    } else if (element.isJsonPrimitive()) {
      map.put(Joiner.on('.').join(path), element.getAsString());
    }
  }

  private static void flatten(JsonObject object, Deque<String> path, Map<String, String> map) {
    for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
      path.addLast(entry.getKey());
      flatten(entry.getValue(), path, map);
      path.removeLast();
    }
  }

  private static void flatten(JsonArray array, Deque<String> path, Map<String, String> map) {
    String top = !path.isEmpty() ? path.removeLast() : null;

    int i = 0;
    for (JsonElement element : array) {
      path.addLast(String.format("%s[%s]", top != null ? top : "", i++));
      flatten(element, path, map);
      path.removeLast();
    }

    if (top != null) {
      path.addLast(top);
    }
  }

  /**
   * Create JsonObject from map where keys are (nested) json object keys and values are object
   * values. E.g. mapping form "foo.bar" => "value" returns json object "{"foo":{"bar":"value"}}.
   *
   * @param map of key value pairs, where keys can represent nested objects separated by dots
   * @return un-flattened json object
   */
  public static JsonElement unflatten(Map<String, String> map) {
    // root element that is returned
    JsonElement result = null;

    for (Map.Entry<String, String> entry : map.entrySet()) {

      // tokenize into list of object field names and array indices
      Iterator<Object> path = tokenize(entry.getKey()).iterator();

      Object property = path.next();

      // In the first round, init the root object/array
      if (result == null) {
        result = newElementFor(property);
      }

      JsonElement target = result;

      // init nested object/array structure and find the target to add the value
      while (path.hasNext()) {
        Object nextProperty = path.next();
        target = ensure(target, property, nextProperty);
        property = nextProperty;
      }

      set(target, property, new JsonPrimitive(entry.getValue()));
    }

    return result;
  }

  private static List<Object> tokenize(String property) {
    Matcher m = PROPERTY_PATH_PATTERN.matcher(property);
    List<Object> tokens = Lists.newArrayList();
    while (m.find()) {
      if (m.groupCount() == 2) {
        tokens.add(m.group(1) != null ? m.group(1) : new Integer(m.group(2)));
      }
    }
    return tokens;
  }

  private static JsonElement newElementFor(Object property) {
    return property instanceof Integer ? new JsonArray() : new JsonObject();
  }

  private static JsonElement set(JsonElement target, Object property, JsonElement value) {
    if (target.isJsonObject()) {
      return set(target.getAsJsonObject(), (String) property, value);
    }
    if (target.isJsonArray()) {
      return set(target.getAsJsonArray(), (Integer) property, value);
    }

    throw new IllegalStateException("Failed to set: " + target + ", " + property + ", " + value);
  }

  private static JsonElement ensure(JsonElement target, Object property, Object nextProperty) {
    JsonElement initValue = newElementFor(nextProperty);

    if (target.isJsonObject()) {
      return ensure(target.getAsJsonObject(), (String) property, initValue);
    }
    if (target.isJsonArray()) {
      return ensure(target.getAsJsonArray(), (Integer) property, initValue);
    }

    throw new IllegalStateException(
        "Failed to ensure: " + target + ", " + property + ", " + nextProperty);
  }

  public static JsonElement ensure(JsonObject target, String field, JsonElement initValue) {
    return has(target, field) ? get(target, field) : set(target, field, initValue);
  }

  public static boolean has(JsonObject target, String field) {
    return !JsonNull.INSTANCE.equals(get(target, field));
  }

  public static JsonElement get(JsonObject object, String field) {
    return nullToJsonNull(object.get(field));
  }

  public static JsonElement set(JsonObject object, String field, JsonElement value) {
    object.add(field, value);
    return value;
  }

  private static JsonElement ensure(JsonArray target, Integer index, JsonElement initValue) {
    return has(target, index) ? get(target, index) : set(target, index, initValue);
  }

  public static boolean has(JsonArray array, Integer index) {
    return !JsonNull.INSTANCE.equals(get(array, index));
  }

  public static JsonElement get(JsonArray array, Integer index) {
    return index < array.size() ? nullToJsonNull(array.get(index)) : JsonNull.INSTANCE;
  }

  public static JsonElement set(JsonArray array, Integer index, JsonElement value) {
    while (index >= array.size()) {
      array.add(JsonNull.INSTANCE);
    }
    array.set(index, value);
    return value;
  }

  public static JsonElement nullToJsonNull(JsonElement element) {
    return element == null ? JsonNull.INSTANCE : element;
  }

}
