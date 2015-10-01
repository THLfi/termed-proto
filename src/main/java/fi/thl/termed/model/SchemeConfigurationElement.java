package fi.thl.termed.model;

import org.hibernate.search.annotations.Indexed;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@Indexed
@MappedSuperclass
public class SchemeConfigurationElement extends Resource {

  @ManyToOne
  private SchemeConfiguration configuration;

  private Boolean required;

  private Boolean repeatable;

  public SchemeConfigurationElement() {
  }

  public SchemeConfiguration getConfiguration() {
    return configuration;
  }

  public void setConfiguration(SchemeConfiguration configuration) {
    this.configuration = configuration;
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
