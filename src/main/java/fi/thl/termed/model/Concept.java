package fi.thl.termed.model;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Concept extends Resource {

  private Map<String, Set<PropertyValue>> properties;

  private Concept broader;
  private List<Concept> narrower;

  private List<Concept> related;

  public Concept() {
    this(null);
  }

  public Concept(String id) {
    super(id);
    this.properties = Maps.newHashMap();
    this.broader = null;
    this.narrower = Lists.newArrayList();
    this.related = Lists.newArrayList();
  }

  public Map<String, Set<PropertyValue>> getProperties() {
    return properties;
  }

  public void setProperties(Map<String, Set<PropertyValue>> properties) {
    this.properties = properties;
  }

  public void addProperty(String propertyId, String lang, String value) {
    if (!properties.containsKey(propertyId)) {
      properties.put(propertyId, Sets.<PropertyValue>newHashSet());
    }
    properties.get(propertyId).add(new PropertyValue(lang, value));
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

  public Objects.ToStringHelper toStringHelper() {
    return super.toStringHelper()
        .add("properties", properties)
        .add("broader", broader)
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
           Objects.equal(broader, that.broader) &&
           Objects.equal(related, that.related);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getId(), properties, broader, related);
  }

}
