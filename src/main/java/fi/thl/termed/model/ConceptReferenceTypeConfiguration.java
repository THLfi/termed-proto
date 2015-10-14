package fi.thl.termed.model;

import org.hibernate.search.annotations.Indexed;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Indexed
@Entity
public class ConceptReferenceTypeConfiguration extends SchemeConfigurationElement {

  @ManyToOne
  private ConceptReferenceType conceptReferenceType;

  @ManyToMany
  private List<Scheme> range;

  @Column(name = "primary_hierarchy")
  private Boolean primaryHierarchy;

  public ConceptReferenceTypeConfiguration() {
  }

  public ConceptReferenceType getConceptReferenceType() {
    return conceptReferenceType;
  }

  public void setConceptReferenceType(ConceptReferenceType conceptReferenceType) {
    this.conceptReferenceType = conceptReferenceType;
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
