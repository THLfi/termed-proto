package fi.thl.termed.model;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import fi.thl.termed.util.ResourceIdMatches;

@Indexed
@Entity
public class Concept extends SchemeResource {

  @IndexedEmbedded(includePaths = {"id"})
  @ManyToMany
  @JoinTable(name = "concept_type",
      joinColumns = {@JoinColumn(name = "concept_id")},
      inverseJoinColumns = {@JoinColumn(name = "type_id")})
  private List<Concept> types;

  @ManyToMany(mappedBy = "types")
  private List<Concept> instances;

  @IndexedEmbedded(includePaths = {"id"})
  @ManyToMany
  @JoinTable(name = "concept_part_of",
      joinColumns = {@JoinColumn(name = "concept_id")},
      inverseJoinColumns = {@JoinColumn(name = "part_of_id")})
  private List<Concept> partOf;

  @ManyToMany(mappedBy = "partOf")
  private List<Concept> parts;

  @IndexedEmbedded(includePaths = {"id"})
  @ManyToMany
  @JoinTable(name = "concept_broader_narrower",
      joinColumns = {@JoinColumn(name = "broader_id")},
      inverseJoinColumns = {@JoinColumn(name = "narrower_id")})
  private List<Concept> broader;

  @ManyToMany(mappedBy = "broader")
  private List<Concept> narrower;

  @ManyToMany
  @JoinTable(name = "concept_related",
      joinColumns = {@JoinColumn(name = "concept_id")},
      inverseJoinColumns = {@JoinColumn(name = "related_id")})
  private List<Concept> related;

  @ManyToMany(mappedBy = "related")
  private List<Concept> relatedFrom;

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
    this.types = concept.types;
    this.instances = concept.instances;
    this.partOf = concept.partOf;
    this.parts = concept.parts;
    this.broader = concept.broader;
    this.narrower = concept.narrower;
    this.related = concept.related;
    this.relatedFrom = concept.relatedFrom;
    this.collections = concept.collections;
  }

  public boolean hasTypes() {
    return types != null && !types.isEmpty();
  }

  public List<Concept> getTypes() {
    return types;
  }

  public void setTypes(List<Concept> types) {
    this.types = types;
  }

  public void addType(Concept concept) {
    if (types == null) {
      types = Lists.newArrayList();
    }
    types.add(concept);
  }

  public List<Concept> getInstances() {
    return instances;
  }

  public void setInstances(List<Concept> instances) {
    this.instances = instances;
  }

  public List<Concept> getPartOf() {
    return partOf;
  }

  public void setPartOf(List<Concept> partOf) {
    this.partOf = partOf;
  }

  public void addPartOf(Concept concept) {
    if (partOf == null) {
      partOf = Lists.newArrayList();
    }
    partOf.add(concept);
  }

  public List<Concept> getParts() {
    return parts;
  }

  public void setParts(List<Concept> parts) {
    this.parts = parts;
  }

  public boolean hasBroader() {
    return broader != null && !broader.isEmpty();
  }

  public List<Concept> getBroader() {
    return broader;
  }

  public void setBroader(List<Concept> broader) {
    this.broader = broader;
  }

  public void addBroader(Concept concept) {
    if (broader == null) {
      broader = Lists.newArrayList();
    }
    broader.add(concept);
  }

  public boolean hasNarrower() {
    return narrower != null && !narrower.isEmpty();
  }

  public List<Concept> getNarrower() {
    return narrower;
  }

  public void setNarrower(List<Concept> narrower) {
    this.narrower = narrower;
  }

  public void addNarrower(Concept n) {
    if (narrower == null) {
      narrower = Lists.newArrayList();
    }
    narrower.add(n);
  }

  public void removeNarrower(Concept concept) {
    if (narrower == null) {
      return;
    }
    Iterators.removeIf(narrower.iterator(), new ResourceIdMatches(concept.getId()));
  }

  public boolean hasRelated() {
    return related != null && !related.isEmpty();
  }

  public List<Concept> getRelated() {
    return related;
  }

  public void setRelated(List<Concept> related) {
    this.related = related;
  }

  public void addRelated(Concept r) {
    if (related == null) {
      related = Lists.newArrayList();
    }
    related.add(r);
  }

  public void removeRelated(Concept concept) {
    if (related == null) {
      return;
    }
    Iterators.removeIf(related.iterator(), new ResourceIdMatches(concept.getId()));
  }

  public List<Concept> getRelatedFrom() {
    return relatedFrom;
  }

  public void setRelatedFrom(List<Concept> relatedFrom) {
    this.relatedFrom = relatedFrom;
  }

  public boolean hasCollections() {
    return collections != null && !collections.isEmpty();
  }

  public List<Collection> getCollections() {
    return collections;
  }

  public void setCollections(List<Collection> collections) {
    this.collections = collections;
  }


}
