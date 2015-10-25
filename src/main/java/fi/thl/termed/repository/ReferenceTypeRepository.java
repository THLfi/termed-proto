package fi.thl.termed.repository;

import org.springframework.stereotype.Repository;

import fi.thl.termed.domain.ReferenceType;

@Repository("referenceTypes")
public class ReferenceTypeRepository extends HibernateSearchRepository<ReferenceType> {

}
