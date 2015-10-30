package fi.thl.termed.util;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;

import java.util.List;

import fi.thl.termed.domain.PropertyValue;

public class PropertyValueListBridge implements FieldBridge {

  @SuppressWarnings("unchecked")
  @Override
  public void set(String name, Object value, Document doc, LuceneOptions luceneOptions) {
    if (value == null) {
      return;
    }

    // drop tailing "properties" or ".properties"
    String rootName = name.substring(0, name.lastIndexOf("properties"));
    if (rootName.endsWith(".")) {
      rootName = rootName.substring(0, rootName.length() - 1);
    }

    int i = 0;

    for (PropertyValue propertyValue : (List<PropertyValue>) value) {
      luceneOptions.addFieldToDocument(
          rootName + propertyValue.getPropertyId() + "." + propertyValue.getLang(),
          propertyValue.getValue(), doc);

      // non-localized search field
      luceneOptions.addFieldToDocument(
          rootName + propertyValue.getPropertyId(),
          propertyValue.getValue(), doc);

      // for searching from all fields
      luceneOptions.addFieldToDocument(
          rootName + LuceneConstants.ALL,
          propertyValue.getValue(), doc);

      // non-analyzed fields for sorting
      doc.add(new Field(
          rootName + propertyValue.getPropertyId() + "." + propertyValue.getLang() + ".sortable",
          propertyValue.getValue().toLowerCase(), Field.Store.NO, Field.Index.NOT_ANALYZED));
      doc.add(new Field(
          rootName + propertyValue.getPropertyId() + ".sortable",
          propertyValue.getValue(), Field.Store.NO, Field.Index.NOT_ANALYZED));

      // for deserializing stored document
      doc.add(new Field(name + "[" + i + "].property.id",
                        propertyValue.getPropertyId(),
                        Field.Store.YES, Field.Index.NOT_ANALYZED));
      doc.add(new Field(name + "[" + i + "].lang",
                        propertyValue.getLang(),
                        Field.Store.YES, Field.Index.NOT_ANALYZED));
      doc.add(new Field(name + "[" + i + "].value",
                        propertyValue.getValue(),
                        Field.Store.YES, Field.Index.NOT_ANALYZED));
      i++;

    }
  }

}
