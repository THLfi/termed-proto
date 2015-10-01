package fi.thl.termed.util;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

import fi.thl.termed.model.Concept;
import fi.thl.termed.model.ConceptReference;
import fi.thl.termed.model.ConceptReferenceType;
import fi.thl.termed.model.SchemeResource;

public final class ConceptTreeUtils {

  private ConceptTreeUtils() {
  }

  public static Predicate<ConceptReference> newConceptReferenceTypeIdPredicate(
      final String typeId) {
    return new Predicate<ConceptReference>() {
      @Override
      public boolean apply(ConceptReference input) {
        return input.getTypeId().equals(typeId);
      }
    };
  }

//  public static List<Concept> copyConceptTrees(List<Concept> concepts,
//                                               Predicate<ConceptReference> deepReferencePredicate,
//                                               Predicate<ConceptReference> deepReferrerPredicate) {
//    List<Concept> newConcepts = Lists.newArrayList();
//
//    for (Concept concept : concepts) {
//      newConcepts.add(copyConceptTree(concept, deepReferencePredicate, deepReferrerPredicate));
//    }
//
//    return newConcepts;
//  }
//
//  public static Concept copyConceptTree(Concept concept,
//                                        Predicate<ConceptReference> deepReferencePredicate,
//                                        Predicate<ConceptReference> deepReferrerPredicate) {
//    return copyConceptTree(concept, deepReferencePredicate, deepReferrerPredicate,
//                           Sets.<Concept>newHashSet());
//  }
//
//  private static Concept copyConceptTree(Concept concept,
//                                         Predicate<ConceptReference> deepReferencePredicate,
//                                         Predicate<ConceptReference> deepReferrerPredicate,
//                                         Set<Concept> copiedConcepts) {
//
//    Concept newConcept = copyTruncatedConcept(concept);
//
//    if (copiedConcepts.contains(concept)) {
//      newConcept.setReferences(copyTruncatedConceptReferences(concept.getReferences()));
//    } else {
//      copiedConcepts.add(concept);
//
//      newConcept.setReferences(
//          copyDeepConceptReferences(concept.getReferences(),
//                                    deepReferencePredicate, deepReferrerPredicate, copiedConcepts));
//      newConcept.setReferrers(
//          copyDeepConceptReferrers(concept.getReferrers(),
//                                   deepReferencePredicate, deepReferrerPredicate, copiedConcepts));
//    }
//
//    return newConcept;
//  }
//
//  private static List<ConceptReference> copyDeepConceptReferences(
//      List<ConceptReference> conceptReferences,
//      Predicate<ConceptReference> deepReferencePredicate,
//      Predicate<ConceptReference> deepReferrerPredicate,
//      Set<Concept> copiedConcepts) {
//
//    List<ConceptReference> newDeepReferences =
//        deepCopyConceptReferenceTargets(
//            ListUtils.filter(conceptReferences, deepReferencePredicate),
//            deepReferencePredicate, deepReferrerPredicate, copiedConcepts);
//
//    List<ConceptReference> newTruncatedReferences =
//        copyTruncatedConceptReferences(
//            ListUtils.filter(conceptReferences, Predicates.not(deepReferencePredicate)));
//
//    return ListUtils.concat(newDeepReferences, newTruncatedReferences);
//  }
//
//  private static List<ConceptReference> copyDeepConceptReferrers(
//      List<ConceptReference> conceptReferrers,
//      Predicate<ConceptReference> deepReferencePredicate,
//      Predicate<ConceptReference> deepReferrerPredicate,
//      Set<Concept> copiedConcepts) {
//
//    List<ConceptReference> newDeepReferrers =
//        deepCopyConceptReferrerSources(
//            ListUtils.filter(conceptReferrers, deepReferrerPredicate),
//            deepReferencePredicate, deepReferrerPredicate, copiedConcepts);
//
//    List<ConceptReference> newTruncatedReferrers =
//        copyTruncatedConceptReferences(
//            ListUtils.filter(conceptReferrers, Predicates.not(deepReferrerPredicate)));
//
//    return ListUtils.concat(newDeepReferrers, newTruncatedReferrers);
//  }
//
//  private static Concept copyTruncatedConcept(Concept concept) {
//    return new Concept(new SchemeResource(concept));
//  }
//
//  private static List<ConceptReference> deepCopyConceptReferenceTargets(
//      List<ConceptReference> references,
//      Predicate<ConceptReference> deepReferencePredicate,
//      Predicate<ConceptReference> deepReferrerPredicate,
//      Set<Concept> copied) {
//
//    List<ConceptReference> newReferences = Lists.newArrayList();
//
//    for (ConceptReference reference : references) {
//      ConceptReference newReference = new ConceptReference();
//      newReference.setType(new ConceptReferenceType(reference.getType()));
//      newReference.setSource(copyTruncatedConcept(reference.getSource()));
//      newReference.setTarget(
//          copyConceptTree(reference.getTarget(), deepReferencePredicate, deepReferrerPredicate,
//                          copied));
//      newReferences.add(newReference);
//    }
//
//    return newReferences;
//  }
//
//  private static List<ConceptReference> deepCopyConceptReferrerSources(
//      List<ConceptReference> referrers,
//      Predicate<ConceptReference> deepReferencePredicate,
//      Predicate<ConceptReference> deepReferrerPredicate,
//      Set<Concept> copied) {
//
//    List<ConceptReference> newReferrers = Lists.newArrayList();
//
//    for (ConceptReference referrer : referrers) {
//      ConceptReference newReferrer = new ConceptReference();
//      newReferrer.setType(new ConceptReferenceType(referrer.getType()));
//      newReferrer.setSource(
//          copyConceptTree(referrer.getSource(), deepReferencePredicate, deepReferrerPredicate,
//                          copied));
//      newReferrer.setTarget(copyTruncatedConcept(referrer.getTarget()));
//      newReferrers.add(newReferrer);
//    }
//
//    return newReferrers;
//  }
//
//  private static List<ConceptReference> copyTruncatedConceptReferences(
//      List<ConceptReference> references) {
//
//    List<ConceptReference> newReferences = Lists.newArrayList();
//
//    for (ConceptReference reference : references) {
//      ConceptReference newReference = new ConceptReference();
//      newReference.setType(new ConceptReferenceType(reference.getType()));
//      newReference.setSource(copyTruncatedConcept(reference.getSource()));
//      newReference.setTarget(copyTruncatedConcept(reference.getTarget()));
//      newReferences.add(newReference);
//    }
//
//    return newReferences;
//  }

}
