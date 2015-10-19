package fi.thl.termed.domain;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class LangValue {

  private String lang;
  private String value;

  public LangValue(String lang, String value) {
    this.lang = lang;
    this.value = value;
  }

  public String getLang() {
    return lang;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(getClass())
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

    LangValue that = (LangValue) o;

    return Objects.equal(lang, that.lang) && Objects.equal(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(lang, value);
  }

}
