package fi.thl.termed.serializer;

import com.google.common.collect.Lists;

import org.junit.Test;

import java.util.List;

import fi.thl.termed.domain.PropertyValue;

import static org.junit.Assert.assertEquals;

public class PropertyListConverterTest {

  private PropertyListConverter propertyListConverter = new PropertyListConverter();

  @Test
  public void shouldConvertAndReverseConvertPropertyList() {
    List<PropertyValue> propertyValueList = Lists.newArrayList();
    propertyValueList.add(new PropertyValue("prefLabel", "en", "Test"));
    propertyValueList.add(new PropertyValue("prefLabel", "fi", "Testi"));
    propertyValueList.add(new PropertyValue("altLabel", "en", "Experiment"));
    propertyValueList.add(new PropertyValue("altLabel", "en", "Trial"));

    assertEquals(propertyValueList,
                 propertyListConverter.reverse().convert(
                     propertyListConverter.convert(propertyValueList)));
  }

}