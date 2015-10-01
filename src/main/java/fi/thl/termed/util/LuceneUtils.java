package fi.thl.termed.util;

import com.google.common.collect.Lists;

import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

import java.util.List;

public final class LuceneUtils {

  private LuceneUtils() {
  }

  public static Sort buildSort(List<String> orderBy) {
    List<SortField> sortFields = Lists.newArrayList();
    for (String field : orderBy) {
      sortFields.add(new SortField(field, SortField.STRING));
    }
    return new Sort(sortFields.toArray(new SortField[sortFields.size()]));
  }

}
