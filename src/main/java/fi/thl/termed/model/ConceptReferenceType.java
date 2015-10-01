package fi.thl.termed.model;

import org.hibernate.search.annotations.Indexed;

import javax.persistence.Entity;
import javax.persistence.Table;

@Indexed
@Entity
@Table(name = "concept_reference_type")
public class ConceptReferenceType extends UriResource {

  public ConceptReferenceType() {
    super();
  }

  public ConceptReferenceType(String id) {
    super(id);
  }

  public ConceptReferenceType(ConceptReferenceType type) {
    super(type);
  }

}
