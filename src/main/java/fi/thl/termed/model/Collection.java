package fi.thl.termed.model;

import com.google.common.base.Objects;

import org.hibernate.search.annotations.Indexed;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Indexed
@Entity
public class Collection extends PropertyResource {

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

  public List<Concept> getMembers() {
    return members;
  }

  public void setMembers(List<Concept> members) {
    this.members = members;
  }

  public Objects.ToStringHelper toStringHelper() {
    return super.toStringHelper()
        .add("members", members);
  }

}
