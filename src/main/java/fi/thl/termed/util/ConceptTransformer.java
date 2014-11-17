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
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;

import fi.thl.termed.model.Collection;
import fi.thl.termed.model.Concept;
import fi.thl.termed.model.SchemePropertyResource;

public class ConceptTransformer implements JsonDeserializer<Concept>, JsonSerializer<Concept> {

  private class SerializedConcept extends SchemePropertyResource {

    private Concept broader;
    private List<SchemePropertyResource> narrower;
    private List<SchemePropertyResource> related;
    private List<Collection> collections;

    public Concept getBroader() {
      return broader;
    }

    public void setBroader(Concept broader) {
      this.broader = broader;
    }

    public List<SchemePropertyResource> getNarrower() {
      return narrower != null ? narrower : Collections.<SchemePropertyResource>emptyList();
    }

    public void setNarrower(List<SchemePropertyResource> narrower) {
      this.narrower = narrower;
    }

    public List<SchemePropertyResource> getRelated() {
      return related != null ? related : Collections.<SchemePropertyResource>emptyList();
    }

    public void setRelated(List<SchemePropertyResource> related) {
      this.related = related;
    }

    public List<Collection> getCollections() {
      return collections != null ? collections : Collections.<Collection>emptyList();
    }

    public void setCollections(List<Collection> collections) {
      this.collections = collections;
    }

  }

  private final Function<Concept, SchemePropertyResource> conceptToPropertyResource =
      new Function<Concept, SchemePropertyResource>() {
        @Override
        public SchemePropertyResource apply(Concept concept) {
          return new SchemePropertyResource(concept);
        }
      };

  private final Function<SchemePropertyResource, Concept> propertyResourceToConcept =
      new Function<SchemePropertyResource, Concept>() {
        @Override
        public Concept apply(SchemePropertyResource propertyResource) {
          return find(propertyResource);
        }
      };

  private final EntityManager em;
  private final boolean truncated;

  public ConceptTransformer(EntityManager em) {
    this(em, false);
  }

  public ConceptTransformer(EntityManager em, boolean truncated) {
    Preconditions.checkNotNull(em);
    this.em = em;
    this.truncated = truncated;
  }

  private Concept find(SchemePropertyResource propertyResource) {
    return propertyResource != null && propertyResource.getId() != null ?
           em.find(Concept.class, propertyResource.getId()) : null;
  }

  @Override
  public Concept deserialize(JsonElement jsonElement, Type type,
                             JsonDeserializationContext jsonDeserializationContext)
      throws JsonParseException {

    Preconditions.checkState(!truncated,
                             "truncated ConceptTransformer can't deserialize Concepts.");

    SerializedConcept serializedConcept =
        jsonDeserializationContext.deserialize(jsonElement, SerializedConcept.class);

    Concept concept = new Concept();

    concept.setId(serializedConcept.getId());
    concept.setProperties(serializedConcept.getProperties());
    concept.setScheme(serializedConcept.getScheme());
    concept.setBroader(serializedConcept.getBroader());
    concept.setNarrower(transform(serializedConcept.getNarrower(), propertyResourceToConcept));
    concept.setRelated(transform(serializedConcept.getRelated(), propertyResourceToConcept));
    concept.setCollections(serializedConcept.getCollections());

    return concept;
  }

  @Override
  public JsonElement serialize(Concept concept, Type type,
                               JsonSerializationContext jsonSerializationContext) {

    SerializedConcept serializedConcept = new SerializedConcept();

    serializedConcept.setId(concept.getId());
    serializedConcept.setProperties(concept.getProperties());

    if (!truncated) {
      serializedConcept.setScheme(concept.getScheme());
      serializedConcept.setBroader(concept.getBroader());
      serializedConcept.setNarrower(transform(concept.getNarrower(), conceptToPropertyResource));
      serializedConcept.setRelated(transform(concept.getRelated(), conceptToPropertyResource));
      serializedConcept.setCollections(concept.getCollections());
    }

    return jsonSerializationContext.serialize(serializedConcept, SerializedConcept.class);
  }

  public <F, T> List<T> transform(List<F> fromList, Function<F, T> function) {
    return Lists.transform(fromList, function);
  }

}
