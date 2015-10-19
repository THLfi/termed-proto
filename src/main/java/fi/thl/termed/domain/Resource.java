package fi.thl.termed.domain;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Strings;

import org.hibernate.search.annotations.Indexed;

import java.util.UUID;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;

@Indexed
@MappedSuperclass
public class Resource {

  @Id
  private String id;

  public Resource() {
    this.id = null;
  }

  public Resource(String id) {
    this.id = id;
  }

  public Resource(Resource resource) {
    this.id = resource.id;
  }

  public boolean hasId() {
    return !Strings.isNullOrEmpty(id);
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @PrePersist
  public void ensureId() {
    if (!hasId()) {
      setId(UUID.randomUUID().toString());
    }
  }

  public MoreObjects.ToStringHelper toStringHelper() {
    return MoreObjects.toStringHelper(getClass()).add("id", id);
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
