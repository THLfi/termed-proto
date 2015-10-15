package fi.thl.termed.service;

import com.google.common.collect.Maps;

import org.apache.lucene.search.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import fi.thl.termed.model.Concept;
import fi.thl.termed.repository.ConceptRepository;
import fi.thl.termed.repository.HibernateSearchRepository;
import fi.thl.termed.repository.Repository;

@Service
@Transactional
public class CrudServiceImpl implements CrudService {

  @PersistenceContext
  private EntityManager em;

  @javax.annotation.Resource
  private Map<String, Class> collectionClassMap;

  private Map<Class, Repository> repositories = Maps.newHashMap();

  @PostConstruct
  public void init() {
    for (Class cls : collectionClassMap.values()) {
      repositories.put(cls, buildRepository(cls));
    }
  }

  private <T> Repository buildRepository(Class<T> cls) {
    return cls.equals(Concept.class) ?
           new ConceptRepository(new HibernateSearchRepository<Concept>(em, Concept.class)) :
           new HibernateSearchRepository<T>(em, cls);
  }

  @SuppressWarnings("unchecked")
  private <T> Repository<T> getRepository(Class<T> cls) {
    return repositories.get(cls);
  }

  @Override
  public <T> T save(Class<T> cls, T value) {
    return getRepository(cls).save(value);
  }

  @Override
  public <T> Iterable<T> save(Class<T> cls, Iterable<T> values) {
    return getRepository(cls).save(values);
  }

  @Override
  public <T> T get(Class<T> cls, String id) {
    return getRepository(cls).get(id);
  }

  @Override
  public <T> List<T> query(Class<T> cls, String query) {
    return query(cls, query, 0, -1, null);
  }

  @Override
  public <T> List<T> query(Class<T> cls, String query, int first, int max, List<String> orderBy) {
    return getRepository(cls).query(query, first, max, orderBy);
  }

  @Override
  public <T> List<T> query(Class<T> cls, Query query) {
    return query(cls, query, 0, -1, null);
  }

  @Override
  public <T> List<T> query(Class<T> cls, Query query, int first, int max, List<String> orderBy) {
    return getRepository(cls).query(query, first, max, orderBy);
  }

  @Override
  public <T> List<T> queryCached(Class<T> cls, String query) {
    return queryCached(cls, query, 0, -1, null);
  }

  @Override
  public <T> List<T> queryCached(Class<T> cls, String query, int first, int max,
                                 List<String> orderBy) {
    return getRepository(cls).queryCached(query, first, max, orderBy);
  }

  @Override
  public <T> List<T> queryCached(Class<T> cls, Query query) {
    return queryCached(cls, query, 0, -1, null);
  }

  @Override
  public <T> List<T> queryCached(Class<T> cls, Query query, int first, int max,
                                 List<String> orderBy) {
    return getRepository(cls).queryCached(query, first, max, orderBy);
  }

  @Override
  public <T> void remove(Class<T> cls, String id) {
    getRepository(cls).remove(id);
  }

}
