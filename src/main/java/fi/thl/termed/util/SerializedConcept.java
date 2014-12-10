package fi.thl.termed.util;

import fi.thl.termed.model.Concept;
import fi.thl.termed.model.SchemeResource;

/**
 * Type to represent serialized concept. Serialized concept is typically somehow truncated to avoid
 * serialization loops.
 */
public class SerializedConcept extends Concept {

  public SerializedConcept() {
  }

  public SerializedConcept(String id) {
    super(id);
  }

  public SerializedConcept(SchemeResource schemeResource) {
    super(schemeResource);
  }

}
