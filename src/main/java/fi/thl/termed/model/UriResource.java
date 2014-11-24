package fi.thl.termed.model;

import com.google.common.base.Objects;

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

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  @Override
  public Objects.ToStringHelper toStringHelper() {
    return super.toStringHelper().add("uri", uri);
  }

}
