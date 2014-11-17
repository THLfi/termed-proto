package fi.thl.termed.model;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class Concept extends Resource {

  private Map<String, Map<String, String>> properties;

  private Resource type;
  private Resource parent;

  private List<Resource> children;
  private List<Resource> related;

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

  public Resource getType() {
    return type;
  }

  public String getTypeId() {
    return type != null ? type.getId() : null;
  }

  public void setType(Resource type) {
    this.type = type;
  }

  public Resource getParent() {
    return parent;
  }

  public String getParentId() {
    return parent != null ? parent.getId() : null;
  }

  public void setParent(Resource parent) {
    this.parent = parent;
  }

  public List<Resource> getChildren() {
    return children;
  }

  public void setChildren(List<Resource> children) {
    this.children = children;
  }

  public List<Resource> getRelated() {
    return related;
  }

  public void setRelated(List<Resource> related) {
    this.related = related;
  }

  public Objects.ToStringHelper toStringHelper() {
    return super.toStringHelper()
        .add("properties", properties)
        .add("parent", parent)
        .add("type", type)
        .add("related", related);
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
           Objects.equal(type, that.type) &&
           Objects.equal(parent, that.parent) &&
           Objects.equal(related, that.related);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getId(), properties, type, parent, related);
  }

}
