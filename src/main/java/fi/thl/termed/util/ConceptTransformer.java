package fi.thl.termed.util;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import fi.thl.termed.model.Collection;
import fi.thl.termed.model.Concept;
import fi.thl.termed.model.Resource;
import fi.thl.termed.model.SchemePropertyResource;

public class ConceptTransformer implements JsonDeserializer<Concept>, JsonSerializer<Concept> {

  private class SerializedBroaderConcept extends SchemePropertyResource {

    private SerializedBroaderConcept broader;

    public SerializedBroaderConcept(Concept concept) {
      super(concept);
      // recursive constructor, with cycle check, to build the whole ancestor path
      if (concept.getBroader() != null) {
        this.broader =
            new SerializedBroaderConcept(concept.getBroader(), Sets.newHashSet(concept.getId()));
      }
    }

    private SerializedBroaderConcept(Concept concept, Set<String> processed) {
      super(concept);
      if (concept.getBroader() != null && !processed.contains(concept.getBroader().getId())) {
        processed.add(concept.getId());
        this.broader = new SerializedBroaderConcept(concept.getBroader(), processed);
      }
    }

    public SerializedBroaderConcept getBroader() {
      return broader;
    }

    public void setBroader(SerializedBroaderConcept broader) {
      this.broader = broader;
    }
  }

  private class SerializedConcept extends SchemePropertyResource {

    private SerializedBroaderConcept broader;
    private List<SchemePropertyResource> narrower;
    private List<SchemePropertyResource> related;
    private List<SchemePropertyResource> collections;

    public SerializedBroaderConcept getBroader() {
      return broader;
    }

    public void setBroader(SerializedBroaderConcept broader) {
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

    public List<SchemePropertyResource> getCollections() {
      return collections != null ? collections : Collections.<SchemePropertyResource>emptyList();
    }

    public void setCollections(List<SchemePropertyResource> collections) {
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

  private final Function<Collection, SchemePropertyResource> collectionToPropertyResource =
      new Function<Collection, SchemePropertyResource>() {
        @Override
        public SchemePropertyResource apply(Collection concept) {
          return new SchemePropertyResource(concept);
        }
      };

  private final Function<SchemePropertyResource, Concept> propertyResourceToConcept =
      new Function<SchemePropertyResource, Concept>() {
        @Override
        public Concept apply(SchemePropertyResource propertyResource) {
          return findConcept(propertyResource);
        }
      };

  private final Function<SchemePropertyResource, Collection> propertyResourceToCollection =
      new Function<SchemePropertyResource, Collection>() {
        @Override
        public Collection apply(SchemePropertyResource propertyResource) {
          return findCollection(propertyResource);
        }
      };

  private final EntityManager em;

  public ConceptTransformer(EntityManager em) {
    Preconditions.checkNotNull(em);
    this.em = em;
  }

  private Concept findConcept(Resource r) {
    return r != null && r.getId() != null ? em.find(Concept.class, r.getId()) : null;
  }

  private Collection findCollection(Resource r) {
    return r != null && r.getId() != null ? em.find(Collection.class, r.getId()) : null;
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
    concept.setBroader(findConcept(serializedConcept.getBroader()));
    concept.setNarrower(transform(serializedConcept.getNarrower(), propertyResourceToConcept));
    concept.setRelated(transform(serializedConcept.getRelated(), propertyResourceToConcept));
    concept.setCollections(transform(serializedConcept.getCollections(),
                                     propertyResourceToCollection));

    return concept;
  }

  @Override
  public JsonElement serialize(Concept concept, Type type,
                               JsonSerializationContext jsonSerializationContext) {

    SerializedConcept serializedConcept = new SerializedConcept();

    serializedConcept.setId(concept.getId());
    serializedConcept.setProperties(concept.getProperties());
    serializedConcept.setScheme(concept.getScheme());
    serializedConcept.setBroader(
        concept.getBroader() != null ? new SerializedBroaderConcept(concept.getBroader()) : null);
    serializedConcept.setNarrower(transform(concept.getNarrower(), conceptToPropertyResource));
    serializedConcept.setRelated(transform(concept.getRelated(), conceptToPropertyResource));
    serializedConcept.setCollections(transform(concept.getCollections(),
                                               collectionToPropertyResource));

    return jsonSerializationContext.serialize(serializedConcept, SerializedConcept.class);
  }

  public <F, T> List<T> transform(List<F> fromList, Function<F, T> function) {
    return Lists.transform(fromList, function);
  }

}
