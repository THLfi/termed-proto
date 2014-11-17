package fi.thl.termed.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import fi.thl.termed.model.Scheme;

public interface SchemeRepository extends JpaRepository<Scheme, String> {
}
