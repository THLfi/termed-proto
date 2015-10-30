package fi.thl.termed.domain;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.io.Serializable;

public class ConceptReferenceId implements Serializable {

  private String type;

  private String source;

  private String target;

  public ConceptReferenceId() {
  }

  public ConceptReferenceId(String type, String source, String target) {
    this.type = type;
    this.source = source;
    this.target = target;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getTarget() {
    return target;
  }

  public void setTarget(String target) {
    this.target = target;
  }

  public MoreObjects.ToStringHelper toStringHelper() {
    return MoreObjects.toStringHelper(getClass())
        .add("type", type)
        .add("source", source)
        .add("target", target);
  }

  @Override
  public String toString() {
    return toStringHelper().toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ConceptReferenceId that = (ConceptReferenceId) o;

    return Objects.equal(type, that.type) &&
           Objects.equal(source, that.source) &&
           Objects.equal(target, that.target);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(type, source, target);
  }

}
