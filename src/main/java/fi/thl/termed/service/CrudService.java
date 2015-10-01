package fi.thl.termed.service;

import java.util.List;

public interface CrudService {

  <T> T save(Class<T> cls, T value);

  <T> Iterable<T> save(Class<T> cls, Iterable<T> values);

  <T> T get(Class<T> cls, String key);

  <T> List<T> query(Class<T> cls, String query, int first, int max, List<String> orderBy);

  <T> void remove(Class<T> cls, String id);

}
