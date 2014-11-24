package fi.thl.termed.model;

import com.google.common.base.Objects;

import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@Indexed
@MappedSuperclass
public class SchemeResource extends PropertyResource {

  @IndexedEmbedded(includePaths = {"id"})
  @ManyToOne
  private Scheme scheme;

  public SchemeResource() {
    super();
  }

  public SchemeResource(String id) {
    super(id);
  }

  public SchemeResource(SchemeResource propertyResource) {
    super(propertyResource);
    this.scheme = propertyResource.scheme;
  }

  public Scheme getScheme() {
    return scheme;
  }

  public void setScheme(Scheme scheme) {
    this.scheme = scheme;
  }

  @Override
  public Objects.ToStringHelper toStringHelper() {
    return super.toStringHelper().add("scheme", scheme);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    SchemeResource that = (SchemeResource) o;

    return super.equals(o) && Objects.equal(scheme, that.scheme);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(super.hashCode(), scheme);
  }

}
