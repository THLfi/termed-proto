package fi.thl.termed.domain;

import org.hibernate.search.annotations.Indexed;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Indexed
@Entity
public class PropertyConfiguration extends SchemeConfigurationElement {

  @ManyToOne
  private Property property;

  private String regex;

  private Boolean localized;

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

  public Boolean getLocalized() {
    return localized;
  }

  public void setLocalized(Boolean localized) {
    this.localized = localized;
  }

}
