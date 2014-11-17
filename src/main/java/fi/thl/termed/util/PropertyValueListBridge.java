package fi.thl.termed.util;

import org.apache.lucene.document.Document;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;

import java.util.List;

import fi.thl.termed.model.PropertyValue;

public class PropertyValueListBridge implements FieldBridge {

  @SuppressWarnings("unchecked")
  @Override
  public void set(String name, Object value, Document document,
                  LuceneOptions luceneOptions) {
    if (value == null) {
      return;
    }

    List<PropertyValue> propertyValues = (List<PropertyValue>) value;
    for (PropertyValue propertyValue : propertyValues) {
      luceneOptions.addFieldToDocument(
          propertyValue.getPropertyId() + "." + propertyValue.getLang(),
          propertyValue.getValue(), document);

      // index also a non-localized version
      luceneOptions.addFieldToDocument(
          propertyValue.getPropertyId(),
          propertyValue.getValue(), document);

      // for searching from all fields
      luceneOptions.addFieldToDocument(
          LuceneConstants.ALL,
          propertyValue.getValue(), document);
    }
  }

}
