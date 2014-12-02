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

import fi.thl.termed.model.Collection;
import fi.thl.termed.model.Concept;
import fi.thl.termed.model.Resource;
import fi.thl.termed.model.SchemeResource;

public class ConceptTransformer implements JsonDeserializer<Concept>, JsonSerializer<Concept> {

  // define a new class for serialized concept to avoid calling this serializer in loop
  private class SerializedConcept extends Concept {

  }

  private final EntityManager em;

  public ConceptTransformer(EntityManager em) {
    Preconditions.checkNotNull(em);
    this.em = em;
  }

  @Override
  public Concept deserialize(JsonElement jsonElement, Type type,
                             JsonDeserializationContext jsonDeserializationContext)
      throws JsonParseException {

    SerializedConcept serializedConcept =
        jsonDeserializationContext.deserialize(jsonElement, SerializedConcept.class);

    Concept concept = new Concept();
    concept.setId(serializedConcept.getId());
    concept.setUri(serializedConcept.getUri());
    concept.setProperties(serializedConcept.getProperties());
    concept.setScheme(serializedConcept.getScheme());
    concept.setBroader(transform(serializedConcept.getBroader(), findConcept));
    concept.setNarrower(transform(serializedConcept.getNarrower(), findConcept));
    concept.setRelated(transform(serializedConcept.getRelated(), findConcept));
    concept.setCollections(transform(serializedConcept.getCollections(), findCollection));

    return concept;
  }

  @Override
  public JsonElement serialize(Concept concept, Type type,
                               JsonSerializationContext jsonSerializationContext) {

    SerializedConcept serializedConcept = new SerializedConcept();
    serializedConcept.setId(concept.getId());
    serializedConcept.setUri(concept.getUri());
    serializedConcept.setProperties(concept.getProperties());
    serializedConcept.setScheme(concept.getScheme());
    serializedConcept.setBroader(transform(concept.getBroader(), truncateConcept));
    serializedConcept.setNarrower(transform(concept.getNarrower(), truncateConcept));
    serializedConcept.setRelated(transform(concept.getRelated(), truncateConcept));
    serializedConcept.setCollections(transform(concept.getCollections(), truncateCollection));

    return jsonSerializationContext.serialize(serializedConcept, SerializedConcept.class);
  }

  public <F, T> List<T> transform(List<F> fromList, Function<F, T> function) {
    return fromList != null ? Lists.transform(fromList, function) : null;
  }

  private Concept findConcept(Resource r) {
    return r != null && r.getId() != null ? em.find(Concept.class, r.getId()) : null;
  }

  private Collection findCollection(Resource r) {
    return r != null && r.getId() != null ? em.find(Collection.class, r.getId()) : null;
  }

  private Concept truncateConcept(Concept concept) {
    return concept != null ? new Concept(new SchemeResource(concept)) : null;
  }

//  private Concept truncateConceptWithBroader(Concept concept) {
//    Concept truncated = truncateConcept(concept);
//    if (concept != null && concept.getBroader() != null) {
//      truncated.setBroader(truncateConceptWithBroader(concept.getBroader()));
//    }
//    return truncated;
//  }

  private Collection truncateCollection(Collection collection) {
    return collection != null ? new Collection(new SchemeResource(collection)) : null;
  }

  private final Function<Concept, Concept> truncateConcept =
      new Function<Concept, Concept>() {
        @Override
        public Concept apply(Concept concept) {
          return truncateConcept(concept);
        }
      };

  private final Function<Collection, Collection> truncateCollection =
      new Function<Collection, Collection>() {
        @Override
        public Collection apply(Collection collection) {
          return truncateCollection(collection);
        }
      };

  private final Function<Concept, Concept> findConcept =
      new Function<Concept, Concept>() {
        @Override
        public Concept apply(Concept propertyResource) {
          return findConcept(propertyResource);
        }
      };

  private final Function<Collection, Collection> findCollection =
      new Function<Collection, Collection>() {
        @Override
        public Collection apply(Collection propertyResource) {
          return findCollection(propertyResource);
        }
      };

}
