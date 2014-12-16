package fi.thl.termed.model;

/**
 * Type to represent serialized concept. Serialized concept is typically truncated to avoid
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
