package fi.thl.termed.serializer;

import com.google.common.base.Converter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

import fi.thl.termed.model.LangValue;
import fi.thl.termed.model.PropertyValue;

public class PropertyListConverter
    extends Converter<List<PropertyValue>, Map<String, List<LangValue>>> {

  @Override
  protected Map<String, List<LangValue>> doForward(List<PropertyValue> properties) {
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

    return propertyMap;
  }

  @Override
  protected List<PropertyValue> doBackward(Map<String, List<LangValue>> propertyMap) {
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
