package fi.thl.termed.util;

import com.google.common.collect.Lists;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;

import java.util.List;

public class LuceneQueryBuilder {

  public class InnerQueryBuilder {

    private Query query;

    private InnerQueryBuilder() {
    }

    public LuceneQueryBuilder term(String field, String value) {
      this.query = new TermQuery(new Term(field, value));
      return LuceneQueryBuilder.this;
    }

    public LuceneQueryBuilder prefix(String field, String value) {
      this.query = new PrefixQuery(new Term(field, value));
      return LuceneQueryBuilder.this;
    }

    public LuceneQueryBuilder all() {
      this.query = new MatchAllDocsQuery();
      return LuceneQueryBuilder.this;
    }

    public LuceneQueryBuilder anyValueOfField(String field) {
      this.query = new TermRangeQuery(field, null, null, true, true);
      return LuceneQueryBuilder.this;
    }

    private Query build() {
      return query;
    }
  }

  private class BuildableBooleanClause {

    private InnerQueryBuilder innerQueryBuilder;
    private BooleanClause.Occur occur;

    private BuildableBooleanClause(InnerQueryBuilder innerQueryBuilder,
                                  BooleanClause.Occur occur) {
      this.innerQueryBuilder = innerQueryBuilder;
      this.occur = occur;
    }

    private BooleanClause build() {
      return new BooleanClause(innerQueryBuilder.build(), occur);
    }
  }

  private List<BuildableBooleanClause> clauses = Lists.newArrayList();

  public InnerQueryBuilder mustNotOccur() {
    InnerQueryBuilder queryBuilder = new InnerQueryBuilder();
    clauses.add(new BuildableBooleanClause(queryBuilder, BooleanClause.Occur.MUST_NOT));
    return queryBuilder;
  }

  public InnerQueryBuilder mustOccur() {
    InnerQueryBuilder queryBuilder = new InnerQueryBuilder();
    clauses.add(new BuildableBooleanClause(queryBuilder, BooleanClause.Occur.MUST));
    return queryBuilder;
  }

  public InnerQueryBuilder shouldOccur() {
    InnerQueryBuilder queryBuilder = new InnerQueryBuilder();
    clauses.add(new BuildableBooleanClause(queryBuilder, BooleanClause.Occur.SHOULD));
    return queryBuilder;
  }

  public Query build() {
    BooleanQuery booleanQuery = new BooleanQuery();
    for (BuildableBooleanClause clause : clauses) {
      booleanQuery.add(clause.build());
    }
    return booleanQuery;
  }

}
