package fi.thl.termed.domain;

import org.hibernate.search.annotations.Indexed;

import javax.persistence.Entity;

@Indexed
@Entity
public class ReferenceType extends PropertyResource {

  public ReferenceType() {
    super();
  }

  public ReferenceType(String id) {
    super(id);
  }

  public ReferenceType(ReferenceType type) {
    super(type);
  }

}
