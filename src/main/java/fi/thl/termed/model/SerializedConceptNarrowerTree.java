package fi.thl.termed.model;

import java.util.List;

public class SerializedConceptNarrowerTree extends SchemeResource {

  private List<Concept> narrower;

  public SerializedConceptNarrowerTree(SchemeResource schemeResource) {
    super(schemeResource);
  }

  public List<Concept> getNarrower() {
    return narrower;
  }

  public void setNarrower(List<Concept> narrower) {
    this.narrower = narrower;
  }

}
