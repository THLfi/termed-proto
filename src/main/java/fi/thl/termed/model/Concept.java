package fi.thl.termed.model;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import org.hibernate.search.annotations.Indexed;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Indexed
@Entity
public class Concept extends PropertyResource {

  @ManyToOne
  private Scheme scheme;

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

  public Concept getBroader() {
    return broader;
  }

  public String getBroaderId() {
    return broader != null ? broader.getId() : null;
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

  public List<Collection> getCollections() {
    return collections;
  }

  public void setCollections(List<Collection> collections) {
    this.collections = collections;
  }

  public Scheme getScheme() {
    return scheme;
  }

  public String getSchemeId() {
    return scheme != null ? scheme.getId() : null;
  }

  public void setScheme(Scheme scheme) {
    this.scheme = scheme;
  }

  public Objects.ToStringHelper toStringHelper() {
    return super.toStringHelper()
        .add("broader", broader)
        .add("related", related)
        .add("collections", collections)
        .add("scheme", scheme);
  }

}
