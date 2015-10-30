package fi.thl.termed.domain;

import org.hibernate.search.annotations.Indexed;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@Indexed
@MappedSuperclass
public class SchemeConfigurationElement extends Resource {

  @ManyToOne
  private Scheme scheme;

  private Boolean required;

  private Boolean repeatable;

  public SchemeConfigurationElement() {
  }

  public Scheme getScheme() {
    return scheme;
  }

  public void setScheme(Scheme scheme) {
    this.scheme = scheme;
  }

  public Boolean getRequired() {
    return required;
  }

  public void setRequired(Boolean required) {
    this.required = required;
  }

  public Boolean getRepeatable() {
    return repeatable;
  }

  public void setRepeatable(Boolean repeatable) {
    this.repeatable = repeatable;
  }

}
