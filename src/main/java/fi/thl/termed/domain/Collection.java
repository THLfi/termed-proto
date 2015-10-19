package fi.thl.termed.domain;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.Iterators;

import org.hibernate.search.annotations.Indexed;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import fi.thl.termed.util.ResourceIdMatches;

@Indexed
@Entity
public class Collection extends SchemeResource {

  @ManyToMany
  @JoinTable(
      name = "collection_concept",
      joinColumns = {@JoinColumn(name = "collection_id")},
      inverseJoinColumns = {@JoinColumn(name = "concept_id")})
  private List<Concept> members;

  public Collection() {
    super();
  }

  public Collection(String id) {
    super(id);
  }

  public Collection(SchemeResource schemeResource) {
    super(schemeResource);
  }

  public List<Concept> getMembers() {
    return members;
  }

  public void setMembers(List<Concept> members) {
    this.members = members;
  }

  public void removeMember(Concept concept) {
    if (members == null) {
      return;
    }
    Iterators.removeIf(members.iterator(), new ResourceIdMatches(concept.getId()));
  }

  public MoreObjects.ToStringHelper toStringHelper() {
    return super.toStringHelper().add("members", members);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Collection that = (Collection) o;

    return super.equals(o) && Objects.equal(members, that.members);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(super.hashCode(), members);
  }

}
