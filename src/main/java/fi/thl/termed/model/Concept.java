package fi.thl.termed.model;

import com.google.common.base.Objects;
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
  @JoinTable(name = "concept_broader_narrower",
      joinColumns = {@JoinColumn(name = "broader_id")},
      inverseJoinColumns = {@JoinColumn(name = "narrower_id")})
  private List<Concept> broader;

  @ManyToMany(mappedBy = "broader")
  private List<Concept> narrower;

  // no need to track back references because related
  // should always be duplicated to both directions
  @ManyToMany
  @JoinTable(name = "concept_related")
  private List<Concept> related;

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
    this.broader = concept.broader;
    this.narrower = concept.narrower;
    this.related = concept.related;
    this.collections = concept.collections;
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

  public List<Collection> getCollections() {
    return collections;
  }

  public void setCollections(List<Collection> collections) {
    this.collections = collections;
  }

}
