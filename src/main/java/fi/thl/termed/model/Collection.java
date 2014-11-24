package fi.thl.termed.model;

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
      joinColumns = {@JoinColumn(name = "collection_id")},
      inverseJoinColumns = {@JoinColumn(name = "concept_id")})
  private List<Concept> members;

  public Collection() {
    super();
  }

  public Collection(String id) {
    super(id);
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

  public Objects.ToStringHelper toStringHelper() {
    return super.toStringHelper()
        .add("members", members);
  }


}
