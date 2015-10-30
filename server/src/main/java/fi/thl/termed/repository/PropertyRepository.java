package fi.thl.termed.repository;

import org.springframework.stereotype.Repository;

import fi.thl.termed.domain.Property;

@Repository("properties")
public class PropertyRepository extends HibernateSearchRepository<Property> {

}
