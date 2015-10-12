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

    int i = 0;

    for (ConceptReference conceptReference : (List<ConceptReference>) value) {
      doc.add(new Field(conceptReference.getTypeId() + ".id",
                        conceptReference.getTargetId(),
                        Field.Store.NO, Field.Index.NOT_ANALYZED));

      // for deserializing stored document
      doc.add(new Field(name + "[" + i + "].type.id",
                        conceptReference.getTypeId(),
                        Field.Store.YES, Field.Index.NOT_ANALYZED));
      doc.add(new Field(name + "[" + i + "].source.id",
                        conceptReference.getSourceId(),
                        Field.Store.YES, Field.Index.NOT_ANALYZED));
      doc.add(new Field(name + "[" + i + "].target.id",
                        conceptReference.getTargetId(),
                        Field.Store.YES, Field.Index.NOT_ANALYZED));
      i++;

    }
  }

}