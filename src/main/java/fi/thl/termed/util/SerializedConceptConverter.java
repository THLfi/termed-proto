package fi.thl.termed.util;

import com.google.common.base.Converter;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.List;

import javax.persistence.EntityManager;

import fi.thl.termed.model.Collection;
import fi.thl.termed.model.Concept;
import fi.thl.termed.model.Resource;
import fi.thl.termed.model.SchemeResource;

public class SerializedConceptConverter extends Converter<Concept, SerializedConcept> {

  private final EntityManager em;

  public SerializedConceptConverter(EntityManager em) {
    Preconditions.checkNotNull(em);
    this.em = em;
  }

  @Override
  protected SerializedConcept doForward(Concept concept) {
    SerializedConcept serializedConcept = new SerializedConcept(new SchemeResource(concept));
    serializedConcept.setBroader(transform(concept.getBroader(), truncateConcept));
    serializedConcept.setNarrower(transform(concept.getNarrower(), truncateConcept));
    serializedConcept.setRelated(transform(concept.getRelated(), truncateConcept));
    serializedConcept.setRelatedFrom(transform(concept.getRelatedFrom(), truncateConcept));
    serializedConcept.setCollections(transform(concept.getCollections(), truncateCollection));
    return serializedConcept;
  }

  @Override
  protected Concept doBackward(SerializedConcept serializedConcept) {
    Concept concept = new Concept(new SchemeResource(serializedConcept));
    concept.setBroader(transform(serializedConcept.getBroader(), findConcept));
    concept.setNarrower(transform(serializedConcept.getNarrower(), findConcept));
    concept.setRelated(transform(serializedConcept.getRelated(), findConcept));
    concept.setRelatedFrom(transform(serializedConcept.getRelatedFrom(), findConcept));
    concept.setCollections(transform(serializedConcept.getCollections(), findCollection));
    return concept;
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
