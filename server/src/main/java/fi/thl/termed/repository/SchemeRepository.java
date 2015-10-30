package fi.thl.termed.repository;

import org.springframework.stereotype.Repository;

import fi.thl.termed.domain.Scheme;

@Repository("schemes")
public class SchemeRepository extends HibernateSearchRepository<Scheme> {

}
