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
import fi.thl.termed.model.ConceptReferenceType;
import fi.thl.termed.model.SchemeResource;
import fi.thl.termed.model.SerializedConcept;
import fi.thl.termed.util.ListUtils;
import fi.thl.termed.util.MapUtils;

/**
 * Converts {@code Concept} into json serializable version where references to other concepts and
 * collections are truncated into {@code SchemeResource}.
 */
public class ConceptLoadingConverter extends Converter<Concept, SerializedConcept> {

  private EntityManager em;

  public ConceptLoadingConverter() {
    this(null);
  }

  public ConceptLoadingConverter(EntityManager em) {
    this.em = em;
  }

  @Override
  protected SerializedConcept doForward(Concept concept) {
    SerializedConcept serializedConcept = new SerializedConcept(new SchemeResource(concept));

    serializedConcept.setReferences(toSerializableReferences(ListUtils.nullToEmpty(
        concept.getReferences())));
    serializedConcept.setReferrers(toSerializableReferrers(ListUtils.nullToEmpty(
        concept.getReferrers())));
    serializedConcept.setCollections(truncateCollection(ListUtils.nullToEmpty(
        concept.getCollections())));

    return serializedConcept;
  }

  private Map<String, List<SchemeResource>> toSerializableReferences(List<ConceptReference> refs) {
    Map<String, List<SchemeResource>> referenceMap = Maps.newHashMap();

    for (ConceptReference reference : refs) {
      MapUtils.put(referenceMap, reference.getTypeId(), truncateConcept(reference.getTarget()));
    }

    return referenceMap;
  }

  private Map<String, List<SchemeResource>> toSerializableReferrers(List<ConceptReference> refs) {
    Map<String, List<SchemeResource>> referrerMap = Maps.newHashMap();

    for (ConceptReference referrer : refs) {
      MapUtils.put(referrerMap, referrer.getTypeId(), truncateConcept(referrer.getSource()));
    }

    return referrerMap;
  }

  private SchemeResource truncateConcept(Concept concept) {
    return concept != null ? new SchemeResource(concept) : null;
  }

  private List<SchemeResource> truncateCollection(List<Collection> collections) {
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
    // note that referrers and groups are not populated as
    // they have no effect on persisting this concept

    return concept;
  }

  private List<ConceptReference> fromSerializableReferences(
      Concept source, Map<String, List<SchemeResource>> referenceMap) {

    List<ConceptReference> references = Lists.newArrayList();

    for (Map.Entry<String, List<SchemeResource>> entry : referenceMap.entrySet()) {
      for (SchemeResource target : entry.getValue()) {
        references.add(new ConceptReference(loadReferenceType(entry.getKey()), source,
                                            loadConcept(target)));
      }
    }

    return references;
  }

  private ConceptReferenceType loadReferenceType(String referenceTypeId) {
    return em.find(ConceptReferenceType.class, referenceTypeId);
  }

  private Concept loadConcept(SchemeResource resource) {
    return em.find(Concept.class, resource.getId());
  }

}
