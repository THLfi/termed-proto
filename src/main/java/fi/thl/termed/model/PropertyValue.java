package fi.thl.termed.model;

import com.google.common.base.Objects;

public class PropertyValue {

  private String lang;
  private String value;

  public PropertyValue(String lang, String value) {
    this.lang = lang;
    this.value = value;
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
    return Objects.toStringHelper(getClass())
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

    return Objects.equal(lang, that.lang) && Objects.equal(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(lang, value);
  }

}
