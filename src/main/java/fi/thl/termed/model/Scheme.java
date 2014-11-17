package fi.thl.termed.model;

import org.hibernate.search.annotations.Indexed;

import javax.persistence.Entity;

@Indexed
@Entity
public class Scheme extends PropertyResource {

  public Scheme() {
    this(null);
  }

  public Scheme(String id) {
    super(id);
  }

}
