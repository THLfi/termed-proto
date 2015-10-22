package fi.thl.termed.repository;

import org.springframework.stereotype.Repository;

import fi.thl.termed.domain.ConceptReferenceType;

@Repository("conceptReferenceTypes")
public class ConceptReferenceTypeRepository
    extends HibernateSearchRepository<ConceptReferenceType> {

}
