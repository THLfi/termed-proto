package fi.thl.termed.repository;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.Version;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import fi.thl.termed.domain.AuditedResource;
import fi.thl.termed.util.JsonUtils;
import fi.thl.termed.util.ListUtils;
import fi.thl.termed.util.LuceneConstants;
import fi.thl.termed.util.LuceneUtils;

public class HibernateSearchRepository<T> implements CrudRepository<T> {

  private static final int INDEXER_BATCH_SIZE_TO_LOAD_OBJECTS = 100;

  private Logger log = LoggerFactory.getLogger(getClass());

  @PersistenceContext
  private EntityManager em;

  private Class<T> type;
  private Gson gson;

  @SuppressWarnings("unchecked")
  public HibernateSearchRepository() {
    this.type = (Class<T>) ((ParameterizedType) getClass()
        .getGenericSuperclass()).getActualTypeArguments()[0];
    this.gson = new Gson();
  }

  @PostConstruct
  public void init() {
    if (isEmpty()) {
      reindex();
    }
  }

  @Override
  public Class<T> getType() {
    return type;
  }

  protected EntityManager getEntityManager() {
    return em;
  }

  protected FullTextEntityManager getFullTextEntityManager() {
    return Search.getFullTextEntityManager(em);
  }

  private Analyzer getAnalyzer() {
    return getFullTextEntityManager().getSearchFactory().getAnalyzer(type);
  }

  private QueryParser newQueryParser() {
    return new QueryParser(Version.LUCENE_36, LuceneConstants.ALL, getAnalyzer());
  }

  public boolean isEmpty() {
    return size() == 0;
  }

  public int size() {
    return getFullTextEntityManager().createFullTextQuery(new MatchAllDocsQuery(), type)
        .getResultSize();
  }

  public void reindex() {
    try {
      getFullTextEntityManager().createIndexer(type)
          .batchSizeToLoadObjects(INDEXER_BATCH_SIZE_TO_LOAD_OBJECTS)
          .startAndWait();
    } catch (InterruptedException e) {
      log.error("", e);
    }
  }

  public List<T> queryCached(String query, int first, int max, List<String> orderBy) {
    return queryCached(parseQuery(query), first, max, orderBy);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<T> queryCached(Query query, int first, int max, List<String> orderBy) {
    return loadResults(getFullTextEntityManager()
                           .createFullTextQuery(query, type)
                           .setProjection(FullTextQuery.DOCUMENT)
                           .setSort(buildSort(orderBy))
                           .setFirstResult(first)
                           .setMaxResults(max < 0 ? Integer.MAX_VALUE : max)
                           .getResultList());
  }

  private List<T> loadResults(List<Object[]> cachedResults) {
    List<T> results = Lists.newArrayList();
    for (Object[] result : cachedResults) {
      results.add(loadResults((Document) result[0]));
    }
    return results;
  }

  private T loadResults(Document doc) {
    JsonObject o = JsonUtils.unflatten(LuceneUtils.toMap(doc)).getAsJsonObject();
    return gson.fromJson(o, type);
  }

  @Override
  public List<T> query(String query, int first, int max, List<String> orderBy) {
    return query(parseQuery(query), first, max, orderBy);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<T> query(Query query, int first, int max, List<String> orderBy) {
    return getFullTextEntityManager()
        .createFullTextQuery(query, type)
        .setSort(buildSort(orderBy))
        .setFirstResult(first)
        .setMaxResults(max < 0 ? Integer.MAX_VALUE : max)
        .getResultList();
  }

  private Query parseQuery(String query) {
    try {
      return Strings.isNullOrEmpty(query) ? new MatchAllDocsQuery() : newQueryParser().parse(query);
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
    return em.find(type, id);
  }

  @Override
  public void remove(String id) {
    em.remove(get(id));
  }

}
