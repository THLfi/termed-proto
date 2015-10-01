package fi.thl.termed.repository;

import java.util.List;

public interface Repository<T> {

  T save(T data);

  Iterable<T> save(Iterable<T> data);

  T get(String id);

  List<T> query(String query, int first, int max, List<String> orderBy);

  void remove(String id);

  int size();

}
