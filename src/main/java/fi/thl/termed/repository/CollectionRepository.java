package fi.thl.termed.repository;

import org.springframework.stereotype.Repository;

import fi.thl.termed.domain.Collection;

@Repository("collections")
public class CollectionRepository extends HibernateSearchRepository<Collection> {

}
