package fi.thl.termed.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import fi.thl.termed.model.Concept;

public interface ConceptRepository extends JpaRepository<Concept, String>,
                                           ConceptRepositoryExtended {

  List<Concept> findBySchemeId(String schemeId);

}
