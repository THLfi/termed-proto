package fi.thl.termed.repository;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.Version;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import javax.persistence.EntityManager;

import fi.thl.termed.model.Concept;
import fi.thl.termed.util.LuceneConstants;

public class ConceptIndex {

  private static final int INDEXER_BATCH_SIZE_TO_LOAD_OBJECTS = 100;

  private Logger log = LoggerFactory.getLogger(getClass());

  private EntityManager em;
  private QueryParser queryParser;

  public ConceptIndex(EntityManager em) {
    this.em = em;
    this.queryParser = new QueryParser(Version.LUCENE_36, LuceneConstants.ALL, getAnalyzer());

    if (isEmpty()) {
      reindex();
    }
  }

  private FullTextEntityManager getFullTextEntityManager() {
    return Search.getFullTextEntityManager(em);
  }

  private Analyzer getAnalyzer() {
    return getFullTextEntityManager().getSearchFactory().getAnalyzer(Concept.class);
  }

  public void reindex() {
    log.info("indexing concepts");
    try {
      getFullTextEntityManager().createIndexer(Concept.class)
          .batchSizeToLoadObjects(INDEXER_BATCH_SIZE_TO_LOAD_OBJECTS)
          .startAndWait();
    } catch (InterruptedException e) {
      log.error("", e);
    }
  }

  public Concept index(Concept concept) {
    getFullTextEntityManager().index(concept);
    return concept;
  }

  @SuppressWarnings("unchecked")
  public List<Concept> query(String query, int first, int max, List<String> orderBy) {
    FullTextQuery fullTextQuery =
        getFullTextEntityManager().createFullTextQuery(parseQuery(query), Concept.class);
    return fullTextQuery.setSort(buildSort(orderBy)).setFirstResult(first).setMaxResults(max)
        .getResultList();
  }

  public int size() {
    return getFullTextEntityManager()
        .createFullTextQuery(new MatchAllDocsQuery(), Concept.class).getResultSize();
  }

  public boolean isEmpty() {
    return size() == 0;
  }

  private Query parseQuery(String query) {
    if (Strings.isNullOrEmpty(query)) {
      return new MatchAllDocsQuery();
    }
    try {
      return queryParser.parse(query);
    } catch (ParseException e) {
      log.error("{}", e.getMessage());
      return new TermQuery(new Term(LuceneConstants.ALL, query));
    }
  }

  private Sort buildSort(List<String> orderBy) {
    if (orderBy != null && !orderBy.isEmpty()) {
      List<SortField> sortFields = Lists.newArrayList();
      for (String field : orderBy) {
        sortFields.add(new SortField(field, SortField.STRING));
      }
      return new Sort(sortFields.toArray(new SortField[sortFields.size()]));
    }
    return Sort.RELEVANCE;
  }

}
