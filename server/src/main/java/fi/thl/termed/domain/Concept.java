package fi.thl.termed.domain;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import fi.thl.termed.util.ConceptReferenceListBridge;
import fi.thl.termed.util.ListUtils;

@Indexed
@Entity
public class Concept extends SchemeResource implements Serializable {

  @Field
  @FieldBridge(impl = ConceptReferenceListBridge.class)
  @IndexedEmbedded
  @OneToMany(mappedBy = "source", cascade = CascadeType.ALL)
  private List<ConceptReference> references;

  @OneToMany(mappedBy = "target")
  private List<ConceptReference> referrers;

  @ManyToMany(mappedBy = "members")
  private List<Collection> collections;

  public Concept() {
    super();
  }

  public Concept(String id) {
    super(id);
  }

  public Concept(SchemeResource schemeResource) {
    super(schemeResource);
  }

  public Concept(Concept concept) {
    super(concept);
    this.references = concept.references;
    this.referrers = concept.referrers;
    this.collections = concept.collections;
  }

  public List<ConceptReference> getReferences() {
    return references;
  }

  public List<Concept> getReferencesByType(String typeId) {
    List<Concept> results = Lists.newArrayList();

    for (ConceptReference reference : ListUtils.nullToEmpty(references)) {
      if (typeId.equals(reference.getTypeId())) {
        results.add(reference.getTarget());
      }
    }

    return results;
  }

  public void setReferences(List<ConceptReference> references) {
    this.references = references;
  }

  public void addReferences(ReferenceType type, Concept... targets) {
    if (references == null) {
      references = Lists.newArrayList();
    }

    for (Concept target : targets) {
      references.add(new ConceptReference(type, this, target));
    }
  }

  public List<ConceptReference> getReferrers() {
    return referrers;
  }

  public List<Concept> getReferrersByType(String typeId) {
    List<Concept> results = Lists.newArrayList();

    for (ConceptReference referrer : ListUtils.nullToEmpty(referrers)) {
      if (typeId.equals(referrer.getTypeId())) {
        results.add(referrer.getSource());
      }
    }

    return results;
  }

  public void setReferrers(List<ConceptReference> referrers) {
    this.referrers = referrers;
  }

  public void addReferrers(ReferenceType type, Concept... sources) {
    if (referrers == null) {
      referrers = Lists.newArrayList();
    }

    for (Concept source : sources) {
      referrers.add(new ConceptReference(type, source, this));
    }
  }

  public List<Collection> getCollections() {
    return collections;
  }

  public void setCollections(List<Collection> collections) {
    this.collections = collections;
  }

  @Override
  public MoreObjects.ToStringHelper toStringHelper() {
    return super.toStringHelper().add("references", references);
  }

}
