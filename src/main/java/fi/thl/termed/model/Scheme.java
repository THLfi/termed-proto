package fi.thl.termed.model;

import org.hibernate.search.annotations.Indexed;

import javax.persistence.Entity;

@Indexed
@Entity
public class Scheme extends AuditedResource {

  public Scheme() {
    super();
  }

  public Scheme(String id) {
    super(id);
  }

  public Scheme(Scheme scheme) {
    super(scheme);
  }

}
