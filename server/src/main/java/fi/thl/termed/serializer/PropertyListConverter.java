package fi.thl.termed.serializer;

import com.google.common.base.Converter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

import fi.thl.termed.domain.PropertyValue;
import fi.thl.termed.util.ListUtils;

public class PropertyListConverter
    extends Converter<List<PropertyValue>, Map<String, Map<String, List<String>>>> {

  @Override
  protected Map<String, Map<String, List<String>>> doForward(List<PropertyValue> properties) {
    Map<String, Map<String, List<String>>> propertyMap = Maps.newLinkedHashMap();

    for (PropertyValue property : properties) {
      String id = property.getPropertyId();
      String lang = Strings.nullToEmpty(property.getLang());
      String value = Strings.nullToEmpty(property.getValue());

      if (!value.isEmpty()) {
        if (!propertyMap.containsKey(id)) {
          propertyMap.put(id, Maps.<String, List<String>>newLinkedHashMap());
        }
        if (!propertyMap.get(id).containsKey(lang)) {
          propertyMap.get(id).put(lang, Lists.<String>newArrayList());
        }
        propertyMap.get(id).get(lang).add(value);
      }
    }

    return propertyMap;
  }

  @Override
  protected List<PropertyValue> doBackward(Map<String, Map<String, List<String>>> propertyMap) {
    List<PropertyValue> properties = Lists.newArrayList();

    for (Map.Entry<String, Map<String, List<String>>> property : propertyMap.entrySet()) {
      for (Map.Entry<String, List<String>> langValues : property.getValue().entrySet()) {
        for (String value : ListUtils.nullToEmpty(langValues.getValue())) {
          if (!Strings.isNullOrEmpty(value)) {
            properties.add(new PropertyValue(property.getKey(), langValues.getKey(), value));
          }
        }
      }
    }

    return properties;
  }

}