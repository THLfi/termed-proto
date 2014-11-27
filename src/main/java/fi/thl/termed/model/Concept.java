package fi.thl.termed.model;

import com.google.common.base.Objects;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import fi.thl.termed.util.LuceneConstants;
import fi.thl.termed.util.ResourceIdMatches;

@Indexed
@Entity
public class Concept extends SchemeResource {

  @IndexedEmbedded(includePaths = {"id"}, indexNullAs = LuceneConstants.NULL)
  @ManyToOne
  private Concept broader;

  @OneToMany(mappedBy = "broader", cascade = CascadeType.ALL)
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

  public Concept getBroader() {
    return broader;
  }

  public void setBroader(Concept broader) {
    this.broader = broader;
  }

  public List<Concept> getNarrower() {
    return narrower;
  }

  public void setNarrower(List<Concept> narrower) {
    this.narrower = narrower;
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

  public void removeRelated(final Concept concept) {
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

  public Objects.ToStringHelper toStringHelper() {
    return super.toStringHelper()
        .add("broader", broader)
        .add("related", related)
        .add("collections", collections);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Concept that = (Concept) o;

    return super.equals(o) &&
           Objects.equal(broader, that.broader) &&
           Objects.equal(narrower, that.narrower) &&
           Objects.equal(related, that.related) &&
           Objects.equal(collections, that.collections);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(super.hashCode(), broader, narrower, related, collections);
  }

}
