package fi.thl.termed.model;

import com.google.common.base.Objects;

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
    return id != null;
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

  public Objects.ToStringHelper toStringHelper() {
    return Objects.toStringHelper(getClass()).add("id", id);
  }

  @Override
  public String toString() {
    return toStringHelper().toString();
  }

}
