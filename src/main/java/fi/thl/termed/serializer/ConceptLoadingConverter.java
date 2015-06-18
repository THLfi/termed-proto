package fi.thl.termed.serializer;

import com.google.common.base.Converter;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import fi.thl.termed.model.Collection;
import fi.thl.termed.model.Concept;
import fi.thl.termed.model.ConceptReference;
import fi.thl.termed.model.Resource;
import fi.thl.termed.model.SchemeResource;
import fi.thl.termed.model.SerializedConcept;
import fi.thl.termed.util.ListUtils;
import fi.thl.termed.util.MapUtils;

/**
 * Converts {@code Concept} into json serializable version where references to other concepts and
 * collections are truncated into {@code SchemeResource}. In reverse conversion, referenced values
 * are restored from database using on value IDs.
 */
public class ConceptLoadingConverter extends Converter<Concept, SerializedConcept> {

  private final EntityManager em;

  public ConceptLoadingConverter(EntityManager em) {
    this.em = em;
  }

  public ConceptLoadingConverter() {
    this.em = null;
  }

  @Override
  protected SerializedConcept doForward(Concept concept) {
    SerializedConcept serializedConcept = new SerializedConcept(new SchemeResource(concept));

    serializedConcept.setReferences(toSerializableReferences(ListUtils.nullToEmpty(
        concept.getReferences())));
    serializedConcept.setReferrers(toSerializableReferrers(ListUtils.nullToEmpty(
        concept.getReferrers())));
    serializedConcept.setCollections(toSerializableCollections(ListUtils.nullToEmpty(
        concept.getCollections())));

    return serializedConcept;
  }

  private Map<String, List<SchemeResource>> toSerializableReferences(
      List<ConceptReference> references) {
    Map<String, List<SchemeResource>> referenceMap = Maps.newHashMap();

    for (ConceptReference reference : references) {
      MapUtils.put(referenceMap, reference.getTypeId(), truncateConcept(reference.getTarget()));
    }

    return referenceMap;
  }

  private Map<String, List<SchemeResource>> toSerializableReferrers(
      List<ConceptReference> referrers) {
    Map<String, List<SchemeResource>> referrerMap = Maps.newHashMap();

    for (ConceptReference referrer : referrers) {
      MapUtils.put(referrerMap, referrer.getTypeId(), truncateConcept(referrer.getSource()));
    }

    return referrerMap;
  }

  private SchemeResource truncateConcept(Concept concept) {
    return concept != null ? new SchemeResource(concept) : null;
  }

  private List<SchemeResource> toSerializableCollections(List<Collection> collections) {
    List<SchemeResource> results = Lists.newArrayList();

    for (Collection collection : collections) {
      results.add(new SchemeResource(collection));
    }

    return results;
  }

  @Override
  protected Concept doBackward(SerializedConcept serializedConcept) {
    Preconditions.checkNotNull(em, "Can't restore references without entity manager.");

    Concept concept = new Concept(new SchemeResource(serializedConcept));

    concept.setReferences(fromSerializableReferences(concept, MapUtils.nullToEmpty(
        serializedConcept.getReferences())));
    concept.setReferrers(fromSerializableReferrers(concept, MapUtils.nullToEmpty(
        serializedConcept.getReferrers())));
    concept.setCollections(fromSerializableCollections(ListUtils.nullToEmpty(
        serializedConcept.getCollections())));

    return concept;
  }

  private List<ConceptReference> fromSerializableReferences(
      Concept source, Map<String, List<SchemeResource>> referenceMap) {

    List<ConceptReference> references = Lists.newArrayList();

    for (Map.Entry<String, List<SchemeResource>> entry : referenceMap.entrySet()) {
      for (SchemeResource target : entry.getValue()) {
        references.add(new ConceptReference(entry.getKey(), source, loadConcept(target)));
      }
    }

    return references;
  }

  private List<ConceptReference> fromSerializableReferrers(
      Concept target, Map<String, List<SchemeResource>> referrerMap) {

    List<ConceptReference> referrers = Lists.newArrayList();

    for (Map.Entry<String, List<SchemeResource>> entry : referrerMap.entrySet()) {
      for (SchemeResource source : entry.getValue()) {
        referrers.add(new ConceptReference(entry.getKey(), loadConcept(source), target));
      }
    }

    return referrers;
  }

  private Concept loadConcept(Resource resource) {
    return em.find(Concept.class, resource.getId());
  }

  private List<Collection> fromSerializableCollections(List<SchemeResource> collections) {
    List<Collection> results = Lists.newArrayList();

    for (SchemeResource collection : collections) {
      results.add(loadCollection(collection));
    }

    return results;
  }

  private Collection loadCollection(Resource resource) {
    return em.find(Collection.class, resource.getId());
  }

}
