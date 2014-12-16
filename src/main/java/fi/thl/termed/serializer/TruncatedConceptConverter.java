package fi.thl.termed.serializer;

import com.google.common.base.Converter;

import fi.thl.termed.model.Concept;
import fi.thl.termed.model.SchemeResource;

/**
 * Converts {@code Concept} to {@code SchemeResource} and back. Conversion is obviously "lossy".
 */
public class TruncatedConceptConverter extends Converter<Concept, SchemeResource> {

  @Override
  protected SchemeResource doForward(Concept concept) {
    return new SchemeResource(concept);
  }

  @Override
  protected Concept doBackward(SchemeResource schemeResource) {
    return new Concept(schemeResource);
  }

}
