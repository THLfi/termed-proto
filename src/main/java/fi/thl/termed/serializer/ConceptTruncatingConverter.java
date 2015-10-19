package fi.thl.termed.serializer;

import com.google.common.base.Converter;

import fi.thl.termed.domain.Concept;
import fi.thl.termed.domain.SchemeResource;

/**
 * Converts {@code Concept} to {@code SchemeResource}.
 */
public class ConceptTruncatingConverter extends Converter<Concept, SchemeResource> {

  @Override
  protected SchemeResource doForward(Concept concept) {
    return new SchemeResource(concept);
  }

  @Override
  protected Concept doBackward(SchemeResource schemeResource) {
    throw new UnsupportedOperationException();
  }

}
