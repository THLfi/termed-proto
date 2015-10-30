package fi.thl.termed.domain;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@Indexed
@MappedSuperclass
public class SchemeResource extends AuditedResource {

  @IndexedEmbedded(includePaths = {"id", "uri", "properties"})
  @ManyToOne
  private Scheme scheme;

  public SchemeResource() {
    super();
  }

  public SchemeResource(String id) {
    super(id);
  }

  public SchemeResource(AuditedResource auditedResource) {
    super(auditedResource);
  }

  public SchemeResource(SchemeResource schemeResource) {
    super(schemeResource);
    this.scheme = schemeResource.scheme;
  }

  public Scheme getScheme() {
    return scheme;
  }

  public void setScheme(Scheme scheme) {
    this.scheme = scheme;
  }

  @Override
  public MoreObjects.ToStringHelper toStringHelper() {
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
