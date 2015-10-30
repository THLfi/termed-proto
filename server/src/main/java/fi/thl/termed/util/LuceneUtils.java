package fi.thl.termed.util;

import com.google.common.base.Joiner;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class LuceneUtils {

  private LuceneUtils() {
  }

  /**
   * Parse all document fields into map. Field name is used as key,
   * field string value is used as value. Multiple values are separated by comma.
   *
   * @param doc to be parsed.
   * @return map with document fields.
   */
  public static Map<String, String> toMap(Document doc) {
    Map<String, String> map = Maps.newHashMap();
    for (Map.Entry<String, Collection<String>> entry : toMultimap(doc).asMap().entrySet()) {
      map.put(entry.getKey(), Joiner.on(',').join(entry.getValue()));
    }
    return map;
  }

  /**
   * Parse all document fields into multimap. Field name is used as key,
   * field string value is used as value.
   *
   * @param doc to be parsed.
   * @return multimap with document fields.
   */
  public static Multimap<String, String> toMultimap(Document doc) {
    Multimap<String, String> map = LinkedHashMultimap.create();
    for (Fieldable field : doc.getFields()) {
      map.put(field.name(), field.stringValue());
    }
    return map;
  }

  public static Sort buildSort(List<String> orderBy) {
    List<SortField> sortFields = Lists.newArrayList();
    for (String field : orderBy) {
      sortFields.add(new SortField(field, SortField.STRING));
    }
    return new Sort(sortFields.toArray(new SortField[sortFields.size()]));
  }

}
