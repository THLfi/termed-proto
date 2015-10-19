package fi.thl.termed.util;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

public final class LuceneQueryUtils {

  private LuceneQueryUtils() {
  }

  public static Query term(String field, String value) {
    return new TermQuery(new Term(field, value));
  }

  public static Query all() {
    return new MatchAllDocsQuery();
  }

}
