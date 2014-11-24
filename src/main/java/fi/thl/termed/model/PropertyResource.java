package fi.thl.termed.model;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;

import fi.thl.termed.util.PropertyValueListBridge;

@Indexed
@MappedSuperclass
public class PropertyResource extends UriResource {

  @Field
  @FieldBridge(impl = PropertyValueListBridge.class)
  @IndexedEmbedded
  @ElementCollection
  @CollectionTable(joinColumns = @JoinColumn(name = "subject_id"))
  private List<PropertyValue> properties;

  public PropertyResource() {
    super();
  }

  public PropertyResource(String id) {
    super(id);
  }

  public PropertyResource(PropertyResource propertyResource) {
    super(propertyResource);
    this.properties = propertyResource.properties;
  }

  public List<PropertyValue> getProperties() {
    return properties;
  }

  public void setProperties(List<PropertyValue> properties) {
    this.properties = properties;
  }

  public void addProperty(String propertyId, String lang, String value) {
    if (properties == null) {
      properties = Lists.newArrayList();
    }
    properties.add(new PropertyValue(propertyId, lang, value));
  }

  public Objects.ToStringHelper toStringHelper() {
    return super.toStringHelper().add("properties", properties);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    PropertyResource that = (PropertyResource) o;

    return super.equals(o) && Objects.equal(properties, that.properties);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(super.hashCode(), properties);
  }

}
