package fi.thl.termed.model;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import org.hibernate.search.annotations.Indexed;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Indexed
@Embeddable
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"property_id", "lang", "value"}))
public class PropertyValue {

  private static final int LANGUAGE_CODE_LENGTH = 2;
  private static final int PROPERTY_VALUE_LENGTH = 2000;

  @ManyToOne
  @JoinColumn(name = "property_id")
  private Property property;

  @Column(length = LANGUAGE_CODE_LENGTH)
  private String lang;

  @Column(length = PROPERTY_VALUE_LENGTH)
  private String value;

  public PropertyValue() {
  }

  public PropertyValue(String propertyId, String lang, String value) {
    this.property = new Property(propertyId);
    this.lang = lang;
    this.value = value;
  }

  public Property getProperty() {
    return property;
  }

  public String getPropertyId() {
    return property != null ? property.getId() : null;
  }

  public void setProperty(Property property) {
    this.property = property;
  }

  public String getLang() {
    return lang;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String toString() {
    return MoreObjects.toStringHelper(getClass())
        .add("propertyId", getPropertyId())
        .add("lang", lang)
        .add("value", value).toString();
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    PropertyValue that = (PropertyValue) o;

    return Objects.equal(property, that.property) &&
           Objects.equal(lang, that.lang) &&
           Objects.equal(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(property, lang, value);
  }

}
