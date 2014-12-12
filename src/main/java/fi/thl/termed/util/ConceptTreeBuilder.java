package fi.thl.termed.util;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.util.List;

import fi.thl.termed.model.AuditedResource;
import fi.thl.termed.model.Concept;
import fi.thl.termed.model.PropertyResource;
import fi.thl.termed.model.SchemeResource;

public class ConceptTreeBuilder {

  public static List<Concept> buildConceptTreesFor(Concept concept) {
    List<Concept> roots = Lists.newArrayList();
    truncateBroaderConcept(concept, roots);
    return roots;
  }

  private static Concept truncateBroaderConcept(Concept concept, List<Concept> roots) {
    Concept truncated = truncateConcept(concept);
    truncated.setNarrower(transform(concept.getNarrower(), truncateConcept));

    if (concept.hasBroader()) {
      for (Concept broader : concept.getBroader()) {
        Concept truncatedBroader = truncateBroaderConcept(broader, roots);
        truncatedBroader.removeNarrower(truncated);
        truncatedBroader.addNarrower(truncated);
      }
    } else {
      roots.add(truncated);
    }

    return truncated;
  }

  private static <F, T> List<T> transform(List<F> fromList, Function<F, T> function) {
    return fromList != null ? Lists.newArrayList(Lists.transform(fromList, function)) : null;
  }

  private static Concept truncateConcept(Concept concept) {
    return concept != null ? new Concept(
        new SchemeResource(new AuditedResource(new PropertyResource(concept)))) : null;
  }

  private static final Function<Concept, Concept> truncateConcept =
      new Function<Concept, Concept>() {
        @Override
        public Concept apply(Concept concept) {
          return truncateConcept(concept);
        }
      };

}
