package fi.thl.termed.model;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Strings;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.MappedSuperclass;

@Indexed
@MappedSuperclass
public class UriResource extends Resource {

  @Field(analyze = Analyze.NO)
  private String uri;

  public UriResource() {
    super();
  }

  public UriResource(String id) {
    super(id);
  }

  public UriResource(UriResource resource) {
    super(resource);
    this.uri = resource.uri;
  }

  public boolean hasUri() {
    return !Strings.isNullOrEmpty(uri);
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  @Override
  public MoreObjects.ToStringHelper toStringHelper() {
    return super.toStringHelper().add("uri", uri);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    UriResource that = (UriResource) o;

    return super.equals(o) && Objects.equal(uri, that.uri);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(super.hashCode(), uri);
  }

}
