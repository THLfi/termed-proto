package fi.thl.termed.domain;

import org.hibernate.search.annotations.Indexed;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Indexed
@Entity
public class Scheme extends AuditedResource {

  @OneToOne
  private SchemeConfiguration configuration;

  public Scheme() {
    super();
  }

  public Scheme(String id) {
    super(id);
  }

  public Scheme(Scheme scheme) {
    super(scheme);
  }

  public SchemeConfiguration getConfiguration() {
    return configuration;
  }

  public void setConfiguration(SchemeConfiguration configuration) {
    this.configuration = configuration;
  }

}
