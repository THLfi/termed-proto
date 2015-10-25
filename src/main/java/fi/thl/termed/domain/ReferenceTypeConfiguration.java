package fi.thl.termed.domain;

import org.hibernate.search.annotations.Indexed;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Indexed
@Entity
public class ReferenceTypeConfiguration extends SchemeConfigurationElement {

  @ManyToOne
  private ReferenceType referenceType;

  @ManyToMany
  private List<Scheme> range;

  private Boolean primaryHierarchy;

  public ReferenceTypeConfiguration() {
  }

  public ReferenceType getReferenceType() {
    return referenceType;
  }

  public void setReferenceType(ReferenceType referenceType) {
    this.referenceType = referenceType;
  }

  public List<Scheme> getRange() {
    return range;
  }

  public void setRange(List<Scheme> range) {
    this.range = range;
  }

  public Boolean getPrimaryHierarchy() {
    return primaryHierarchy;
  }

  public void setPrimaryHierarchy(Boolean primaryHierarchy) {
    this.primaryHierarchy = primaryHierarchy;
  }

}
