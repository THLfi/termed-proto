package fi.thl.termed.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fi.thl.termed.model.Concept;

public interface ConceptRepository extends JpaRepository<Concept, String> {

}
