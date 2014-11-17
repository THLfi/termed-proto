package fi.thl.termed.dao;

import java.util.List;

import fi.thl.termed.model.Concept;

public interface ConceptDao {

  Concept save(Concept concept);

  Concept findOne(String id);

  List<Concept> findAll();

  boolean exists(String id);

  void remove(String id);

}
