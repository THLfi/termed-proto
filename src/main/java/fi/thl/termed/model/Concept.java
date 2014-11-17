package fi.thl.termed.model;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class Concept extends Resource {

  private Map<String, Map<String, String>> properties;

  private Concept type;
  private Concept parent;

  private List<Concept> children;
  private List<Concept> related;

  public Concept() {
    this(null);
  }

  public Concept(String id) {
    super(id);
    this.properties = Maps.newHashMap();
    this.type = null;
    this.parent = null;
    this.children = Lists.newArrayList();
    this.related = Lists.newArrayList();
  }

  public Map<String, Map<String, String>> getProperties() {
    return properties;
  }

  public void setProperties(Map<String, Map<String, String>> properties) {
    this.properties = properties;
  }

  public Concept getType() {
    return type;
  }

  public String getTypeId() {
    return type != null ? type.getId() : null;
  }

  public void setType(Concept type) {
    this.type = type;
  }

  public Concept getParent() {
    return parent;
  }

  public String getParentId() {
    return parent != null ? parent.getId() : null;
  }

  public void setParent(Concept parent) {
    this.parent = parent;
  }

  public List<Concept> getChildren() {
    return children;
  }

  public void setChildren(List<Concept> children) {
    this.children = children;
  }

  public List<Concept> getRelated() {
    return related;
  }

  public List<String> getRelatedIds() {
    return related != null ? Lists.transform(related, new Function<Concept, String>() {
      @Override
      public String apply(Concept concept) {
        return concept.getId();
      }
    }) : null;
  }

  public void setRelated(List<Concept> related) {
    this.related = related;
  }

  public Objects.ToStringHelper toStringHelper() {
    return super.toStringHelper()
        .add("properties", properties)
        .add("parentId", getParentId())
        .add("typeId", getTypeId())
        .add("relatedIds", getRelatedIds());
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

    return Objects.equal(getId(), that.getId()) &&
           Objects.equal(properties, that.properties) &&
           Objects.equal(getTypeId(), that.getTypeId()) &&
           Objects.equal(getParentId(), that.getParentId()) &&
           Objects.equal(getRelatedIds(), that.getRelatedIds());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getId(), properties, getTypeId(), getParentId(), getRelatedIds());
  }

}
