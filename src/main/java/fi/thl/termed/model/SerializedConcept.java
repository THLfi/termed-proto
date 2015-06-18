package fi.thl.termed.model;

import java.util.List;
import java.util.Map;

public class SerializedConcept extends SchemeResource {

  private Map<String, List<SchemeResource>> references;

  private Map<String, List<SchemeResource>> referrers;

  private List<SchemeResource> collections;

  public SerializedConcept() {
  }

  public SerializedConcept(String id) {
    super(id);
  }

  public SerializedConcept(SchemeResource schemeResource) {
    super(schemeResource);
  }

  public Map<String, List<SchemeResource>> getReferences() {
    return references;
  }

  public void setReferences(Map<String, List<SchemeResource>> references) {
    this.references = references;
  }

  public Map<String, List<SchemeResource>> getReferrers() {
    return referrers;
  }

  public void setReferrers(Map<String, List<SchemeResource>> referrers) {
    this.referrers = referrers;
  }

  public List<SchemeResource> getCollections() {
    return collections;
  }

  public void setCollections(List<SchemeResource> collections) {
    this.collections = collections;
  }

}
