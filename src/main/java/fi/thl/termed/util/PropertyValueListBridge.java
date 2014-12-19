package fi.thl.termed.util;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;

import java.util.List;

import fi.thl.termed.model.PropertyValue;

public class PropertyValueListBridge implements FieldBridge {

  @SuppressWarnings("unchecked")
  @Override
  public void set(String name, Object value, Document doc, LuceneOptions luceneOptions) {
    if (value == null) {
      return;
    }

    for (PropertyValue propertyValue : (List<PropertyValue>) value) {
      luceneOptions.addFieldToDocument(
          propertyValue.getPropertyId() + "." + propertyValue.getLang(),
          propertyValue.getValue(), doc);

      // non-localized search field
      luceneOptions.addFieldToDocument(
          propertyValue.getPropertyId(),
          propertyValue.getValue(), doc);

      // for searching from all fields
      luceneOptions.addFieldToDocument(
          LuceneConstants.ALL,
          propertyValue.getValue(), doc);

      // non-analyzed fields for sorting
      doc.add(new Field(propertyValue.getPropertyId() + "." + propertyValue.getLang() + ".sortable",
                        propertyValue.getValue().toLowerCase(), Field.Store.NO,
                        Field.Index.NOT_ANALYZED));
      doc.add(new Field(propertyValue.getPropertyId() + ".sortable",
                        propertyValue.getValue(), Field.Store.NO, Field.Index.NOT_ANALYZED));
    }
  }

}
