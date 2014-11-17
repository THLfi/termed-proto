package fi.thl.termed.model;

import org.hibernate.search.annotations.Indexed;

import javax.persistence.Entity;

@Indexed
@Entity
public class Property extends Resource {

  public Property() {
    super();
  }

  public Property(String id) {
    super(id);
  }

}
