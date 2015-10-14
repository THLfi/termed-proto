package fi.thl.termed.model;

import org.hibernate.search.annotations.Indexed;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Indexed
@Entity
public class PropertyConfiguration extends SchemeConfigurationElement {

  @ManyToOne
  private Property property;

  private String regex;

  public PropertyConfiguration() {
  }

  public Property getProperty() {
    return property;
  }

  public void setProperty(Property property) {
    this.property = property;
  }

  public String getRegex() {
    return regex;
  }

  public void setRegex(String regex) {
    this.regex = regex;
  }

}
