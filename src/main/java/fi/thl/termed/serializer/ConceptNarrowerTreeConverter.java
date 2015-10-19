package fi.thl.termed.serializer;

import com.google.common.base.Converter;
import com.google.common.collect.Lists;

import java.util.List;

import fi.thl.termed.domain.Concept;
import fi.thl.termed.domain.SerializedConceptNarrowerTree;

/**
 * Used with JSON exporter to dump whole concept tree using narrower (i.e. inverse of broader)
 * predicate
 */
public class ConceptNarrowerTreeConverter
    extends Converter<Concept, SerializedConceptNarrowerTree> {

  @Override
  protected SerializedConceptNarrowerTree doForward(Concept concept) {
    SerializedConceptNarrowerTree serialized = new SerializedConceptNarrowerTree(concept);

    List<Concept> narrower = Lists.newArrayList();
    for (Concept referrer : concept.getReferrersByType("broader")) {
      narrower.add(referrer);
    }
    serialized.setNarrower(narrower);

    return serialized;
  }

  @Override
  protected Concept doBackward(SerializedConceptNarrowerTree schemeResource) {
    throw new UnsupportedOperationException();
  }

}
