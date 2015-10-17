package fi.thl.termed.util;

import org.apache.lucene.search.Query;
import org.junit.Test;

import static org.junit.Assert.*;

public class LuceneQueryBuilderTest {

  @Test
  public void shouldBuildBooleanQueryWithMultipleTerms() {
    Query q = new LuceneQueryBuilder()
        .mustOccur().term("id", "123")
        .shouldOccur().prefix("name", "Jon")
        .mustNotOccur().anyValueOfField("age").build();

    assertEquals("+id:123 name:Jon* -age:[* TO *]", q.toString());
  }

}