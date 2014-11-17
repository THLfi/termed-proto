package fi.thl.termed.dao;

import java.util.List;

import fi.thl.termed.model.Concept;

public interface ConceptDao {

  Concept save(Concept concept);

  Concept get(String id);

  List<Concept> query();

  List<Concept> query(int max);

  List<Concept> query(String query, int max);

  boolean exists(String id);

  void remove(String id);

}
