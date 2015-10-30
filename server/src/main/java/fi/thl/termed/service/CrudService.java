package fi.thl.termed.service;

import org.apache.lucene.search.Query;

import java.util.List;

public interface CrudService {

  <T> T save(Class<T> cls, T value);

  <T> Iterable<T> save(Class<T> cls, Iterable<T> values);

  <T> T get(Class<T> cls, String key);

  <T> List<T> query(Class<T> cls, String query);

  <T> List<T> query(Class<T> cls, String query, int first, int max, List<String> orderBy);

  <T> List<T> query(Class<T> cls, Query query);

  <T> List<T> query(Class<T> cls, Query query, int first, int max, List<String> orderBy);

  <T> List<T> queryCached(Class<T> cls, String query);

  <T> List<T> queryCached(Class<T> cls, String query, int first, int max, List<String> orderBy);

  <T> List<T> queryCached(Class<T> cls, Query query);

  <T> List<T> queryCached(Class<T> cls, Query query, int first, int max, List<String> orderBy);

  <T> void remove(Class<T> cls, String id);

}
