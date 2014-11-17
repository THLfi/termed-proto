package fi.thl.termed.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import fi.thl.termed.model.Collection;

public interface CollectionRepository extends JpaRepository<Collection, String> {

  List<Collection> findBySchemeId(String schemeId);

}
