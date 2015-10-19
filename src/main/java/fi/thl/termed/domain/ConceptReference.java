package fi.thl.termed.domain;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Indexed;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import fi.thl.termed.util.ResourceFieldBridge;

@Indexed
@Entity
@IdClass(ConceptReferenceId.class)
@Table(name = "concept_references")
public class ConceptReference implements Serializable {

  @FieldBridge(impl = ResourceFieldBridge.class)
  @Id
  @ManyToOne
  private ConceptReferenceType type;

  @FieldBridge(impl = ResourceFieldBridge.class)
  @Id
  @ManyToOne
  private Concept source;

  @FieldBridge(impl = ResourceFieldBridge.class)
  @Id
  @ManyToOne
  private Concept target;

  public ConceptReference() {
  }

  public ConceptReference(ConceptReferenceType type, Concept source, Concept target) {
    this.type = type;
    this.source = source;
    this.target = target;
  }

  public ConceptReference(String typeId, Concept source, Concept target) {
    this(new ConceptReferenceType(typeId), source, target);
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ConceptReference that = (ConceptReference) o;

    return Objects.equal(type, that.type) &&
           Objects.equal(source, that.source) &&
           Objects.equal(target, that.target);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(type, source, target);
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
