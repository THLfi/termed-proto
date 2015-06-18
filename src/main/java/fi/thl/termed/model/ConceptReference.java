package fi.thl.termed.model;

import com.google.common.base.MoreObjects;

import org.hibernate.search.annotations.Indexed;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Indexed
@Entity
@Table(name = "concept_references")
public class ConceptReference extends Resource {

  @ManyToOne
  private ConceptReferenceType type;

  @ManyToOne
  private Concept source;

  @ManyToOne
  private Concept target;

  public ConceptReference() {
  }

  public ConceptReference(String typeId, Concept source, Concept target) {
    this(new ConceptReferenceType(typeId), source, target);
  }

  public ConceptReference(ConceptReferenceType type, Concept source, Concept target) {
    this.type = type;
    this.source = source;
    this.target = target;
  }

  public ConceptReferenceType getType() {
    return type;
  }

  public String getTypeId() {
    return type != null ? type.getId() : null;
  }

  public void setType(ConceptReferenceType type) {
    this.type = type;
  }

  public Concept getSource() {
    return source;
  }

  public String getSourceId() {
    return source != null ? source.getId() : null;
  }

  public void setSource(Concept source) {
    this.source = source;
  }

  public Concept getTarget() {
    return target;
  }

  public String getTargetId() {
    return target != null ? target.getId() : null;
  }

  public void setTarget(Concept target) {
    this.target = target;
  }

  public MoreObjects.ToStringHelper toStringHelper() {
    return MoreObjects.toStringHelper(getClass())
        .add("typeId", getTypeId())
        .add("sourceId", getSourceId())
        .add("targetId", getTargetId());
  }

  @Override
  public String toString() {
    return toStringHelper().toString();
  }

}
