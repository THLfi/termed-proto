package fi.thl.termed.util;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import fi.thl.termed.model.PropertyValue;

public class PropertyValueListTransformer implements JsonSerializer<List<PropertyValue>>,
                                                     JsonDeserializer<List<PropertyValue>> {

  private class LangValue {

    private String lang;
    private String value;

    public LangValue(String lang, String value) {
      this.lang = lang;
      this.value = value;
    }

    public String getLang() {
      return lang;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return Objects.toStringHelper(getClass()).add("lang", lang).add("value", value).toString();
    }

  }

  public static final Type PROPERTY_LIST_TYPE = new TypeToken<List<PropertyValue>>() {
  }.getType();

  public static final Type PROPERTY_MAP_TYPE = new TypeToken<Map<String, List<LangValue>>>() {
  }.getType();

  @Override
  public JsonElement serialize(List<PropertyValue> properties, Type type,
                               JsonSerializationContext jsonSerializationContext) {

    Map<String, List<LangValue>> propertyMap = Maps.newHashMap();

    for (PropertyValue property : properties) {
      String propertyId = property.getPropertyId();
      String propertyLang = Strings.nullToEmpty(property.getLang());
      String propertyValue = Strings.nullToEmpty(property.getValue());

      if (!propertyValue.isEmpty()) {
        if (!propertyMap.containsKey(propertyId)) {
          propertyMap.put(propertyId, Lists.<LangValue>newArrayList());
        }
        propertyMap.get(propertyId).add(new LangValue(propertyLang, propertyValue));
      }
    }

    return jsonSerializationContext.serialize(propertyMap, PROPERTY_MAP_TYPE);
  }

  @Override
  public List<PropertyValue> deserialize(JsonElement element, Type type,
                                         JsonDeserializationContext jsonDeserializationContext) {

    Map<String, List<LangValue>> propertyMap =
        jsonDeserializationContext.deserialize(element, PROPERTY_MAP_TYPE);
    List<PropertyValue> properties = Lists.newArrayList();

    for (Map.Entry<String, List<LangValue>> entry : propertyMap.entrySet()) {
      for (LangValue langValue : entry.getValue()) {
        String propertyId = entry.getKey();
        properties.add(new PropertyValue(propertyId, langValue.getLang(), langValue.getValue()));
      }
    }

    return properties;
  }

}
