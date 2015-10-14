package fi.thl.termed.model;

import org.hibernate.search.annotations.Indexed;

import javax.persistence.Entity;

@Indexed
@Entity
public class ConceptReferenceType extends PropertyResource {

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
