package fi.thl.termed.model;

import com.google.common.base.Objects;

import java.util.UUID;

public class Resource {

  private String id;

  public Resource() {
  }

  public Resource(String id) {
    this.id = id;
  }

  public boolean hasId() {
    return id != null;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void ensureId() {
    if (!hasId()) {
      setId(UUID.randomUUID().toString());
    }
  }

  public Objects.ToStringHelper toStringHelper() {
    return Objects.toStringHelper(getClass()).add("id", id);
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

    Resource that = (Resource) o;

    return Objects.equal(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

}