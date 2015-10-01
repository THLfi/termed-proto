package fi.thl.termed.repository;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.Version;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import javax.persistence.EntityManager;

import fi.thl.termed.model.AuditedResource;
import fi.thl.termed.util.ListUtils;
import fi.thl.termed.util.LuceneConstants;
import fi.thl.termed.util.LuceneUtils;

public class HibernateSearchRepository<T> implements Repository<T> {

  private static final int INDEXER_BATCH_SIZE_TO_LOAD_OBJECTS = 100;

  private Logger log = LoggerFactory.getLogger(getClass());

  private EntityManager em;
  private QueryParser queryParser;

  private Class<T> cls;

  public HibernateSearchRepository(EntityManager em, Class<T> cls) {
    this.em = em;
    this.cls = cls;
    this.queryParser =
        new QueryParser(Version.LUCENE_36, LuceneConstants.ALL,
                        getFullTextEntityManager().getSearchFactory().getAnalyzer(cls));

    if (isEmpty()) {
      reindex();
    }
  }

  protected EntityManager getEntityManager() {
    return em;
  }

  protected FullTextEntityManager getFullTextEntityManager() {
    return Search.getFullTextEntityManager(em);
  }

  public boolean isEmpty() {
    return size() == 0;
  }

  public int size() {
    return getFullTextEntityManager().createFullTextQuery(new MatchAllDocsQuery(), cls)
        .getResultSize();
  }

  public void reindex() {
    try {
      getFullTextEntityManager().createIndexer(cls)
          .batchSizeToLoadObjects(INDEXER_BATCH_SIZE_TO_LOAD_OBJECTS)
          .startAndWait();
    } catch (InterruptedException e) {
      log.error("", e);
    }
  }

  @SuppressWarnings("unchecked")
  public List<T> query(String query, int first, int max, List<String> orderBy) {
    return getFullTextEntityManager()
        .createFullTextQuery(parseQuery(query), cls)
        .setSort(buildSort(orderBy))
        .setFirstResult(first)
        .setMaxResults(max < 0 ? Integer.MAX_VALUE : max)
        .getResultList();
  }

  private Query parseQuery(String query) {
    try {
      return Strings.isNullOrEmpty(query) ? new MatchAllDocsQuery() : queryParser.parse(query);
    } catch (ParseException e) {
      log.warn("{}", e.getMessage());
      return new TermQuery(new Term(LuceneConstants.ALL, query));
    }
  }

  private Sort buildSort(List<String> orderBy) {
    return !ListUtils.isNullOrEmpty(orderBy) ? LuceneUtils.buildSort(orderBy) : Sort.RELEVANCE;
  }

  @Override
  public T save(T data) {
    return index(em.merge(preUpdate(data)));
  }

  @Override
  public Iterable<T> save(Iterable<T> data) {
    List<T> saved = Lists.newArrayList();

    for (T datum : data) {
      saved.add(save(datum));
    }

    return saved;
  }

  private T preUpdate(T resource) {
    if (resource instanceof AuditedResource) {
      ((AuditedResource) resource).resourceUpdated();
    }
    return resource;
  }

  private T index(T stored) {
    getFullTextEntityManager().index(stored);
    return stored;
  }

  @Override
  public T get(String id) {
    return em.find(cls, id);
  }

  @Override
  public void remove(String id) {
    em.remove(get(id));
  }

}