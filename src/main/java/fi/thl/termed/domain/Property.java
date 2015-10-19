package fi.thl.termed.domain;

import org.hibernate.search.annotations.Indexed;

import javax.persistence.Entity;

@Indexed
@Entity
public class Property extends PropertyResource {

  public Property() {
    super();
  }

  public Property(String id) {
    super(id);
  }

}
