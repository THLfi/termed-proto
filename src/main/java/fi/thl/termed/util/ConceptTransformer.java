package fi.thl.termed.util;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.List;

import javax.persistence.EntityManager;

import fi.thl.termed.model.Concept;
import fi.thl.termed.model.PropertyResource;
import fi.thl.termed.model.Scheme;

public class ConceptTransformer implements JsonDeserializer<Concept>, JsonSerializer<Concept> {

  private class SerializedConcept extends PropertyResource {

    private Scheme scheme;
    private Concept broader;
    private List<PropertyResource> narrower;
    private List<PropertyResource> related;

    public Scheme getScheme() {
      return scheme;
    }

    public void setScheme(Scheme scheme) {
      this.scheme = scheme;
    }

    public Concept getBroader() {
      return broader;
    }

    public void setBroader(Concept broader) {
      this.broader = broader;
    }

    public List<PropertyResource> getNarrower() {
      return narrower;
    }

    public void setNarrower(List<PropertyResource> narrower) {
      this.narrower = narrower;
    }

    public List<PropertyResource> getRelated() {
      return related;
    }

    public void setRelated(List<PropertyResource> related) {
      this.related = related;
    }

  }

  private final Function<Concept, PropertyResource> conceptToPropertyResource =
      new Function<Concept, PropertyResource>() {
        @Override
        public PropertyResource apply(Concept concept) {
          return new PropertyResource(concept);
        }
      };

  private final Function<PropertyResource, Concept> propertyResourceToConcept =
      new Function<PropertyResource, Concept>() {
        @Override
        public Concept apply(PropertyResource propertyResource) {
          return find(propertyResource);
        }
      };

  private final EntityManager em;

  public ConceptTransformer(EntityManager em) {
    Preconditions.checkNotNull(em);
    this.em = em;
  }

  private Concept find(PropertyResource propertyResource) {
    return propertyResource != null && propertyResource.getId() != null ?
           em.find(Concept.class, propertyResource.getId()) : null;
  }

  @Override
  public Concept deserialize(JsonElement jsonElement, Type type,
                             JsonDeserializationContext jsonDeserializationContext)
      throws JsonParseException {

    SerializedConcept serializedConcept =
        jsonDeserializationContext.deserialize(jsonElement, SerializedConcept.class);

    Concept concept = new Concept();

    concept.setId(serializedConcept.getId());
    concept.setProperties(serializedConcept.getProperties());
    concept.setScheme(serializedConcept.getScheme());
    concept.setBroader(serializedConcept.getBroader());
    concept.setNarrower(transform(serializedConcept.getNarrower(), propertyResourceToConcept));
    concept.setRelated(transform(serializedConcept.getRelated(), propertyResourceToConcept));

    return concept;
  }

  @Override
  public JsonElement serialize(Concept concept, Type type,
                               JsonSerializationContext jsonSerializationContext) {

    SerializedConcept serializedConcept = new SerializedConcept();

    serializedConcept.setId(concept.getId());
    serializedConcept.setProperties(concept.getProperties());
    serializedConcept.setScheme(concept.getScheme());
    serializedConcept.setBroader(concept.getBroader());
    serializedConcept.setNarrower(transform(concept.getNarrower(), conceptToPropertyResource));
    serializedConcept.setRelated(transform(concept.getRelated(), conceptToPropertyResource));

    return jsonSerializationContext.serialize(serializedConcept, SerializedConcept.class);
  }

  public <F, T> List<T> transform(List<F> fromList, Function<F, T> function) {
    return Lists.transform(fromList, function);
  }

}
