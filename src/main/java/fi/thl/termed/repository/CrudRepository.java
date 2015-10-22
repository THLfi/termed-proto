package fi.thl.termed.repository;

import org.apache.lucene.search.Query;

import java.util.List;

public interface CrudRepository<T> {

  Class<T> getType();

  T save(T data);

  Iterable<T> save(Iterable<T> data);

  T get(String id);

  List<T> query(String query, int first, int max, List<String> orderBy);

  List<T> query(Query query, int first, int max, List<String> orderBy);

  List<T> queryCached(String query, int first, int max, List<String> orderBy);

  List<T> queryCached(Query query, int first, int max, List<String> orderBy);

  void remove(String id);

  int size();

}
