package fi.thl.termed.util;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;

import java.util.List;

import fi.thl.termed.model.ConceptReference;

public class ConceptReferenceListBridge implements FieldBridge {

  @SuppressWarnings("unchecked")
  @Override
  public void set(String name, Object value, Document doc, LuceneOptions luceneOptions) {
    if (value == null) {
      return;
    }

    for (ConceptReference conceptReference : (List<ConceptReference>) value) {
      doc.add(new Field(conceptReference.getTypeId() + ".id",
                        conceptReference.getTargetId(),
                        Field.Store.YES, Field.Index.NOT_ANALYZED));
    }
  }

}