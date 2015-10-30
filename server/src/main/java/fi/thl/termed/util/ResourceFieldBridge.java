package fi.thl.termed.util;

import org.apache.lucene.document.Document;
import org.hibernate.search.bridge.LuceneOptions;
import org.hibernate.search.bridge.TwoWayFieldBridge;

import fi.thl.termed.domain.Resource;

public class ResourceFieldBridge implements TwoWayFieldBridge {

  @SuppressWarnings("unchecked")
  @Override
  public void set(String name, Object value, Document doc, LuceneOptions luceneOptions) {
    if (value == null) {
      return;
    }

    luceneOptions.addFieldToDocument("id", ((Resource) value).getId(), doc);
  }

  @Override
  public Object get(String name, Document document) {
    return new Resource(document.get(name));
  }

  @Override
  public String objectToString(Object object) {
    return ((Resource) object).getId();
  }
}
