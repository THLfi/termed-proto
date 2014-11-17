package fi.thl.termed.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;

import fi.thl.termed.model.Resource;


public class ResourceIdMatches implements Predicate<Resource> {

  private String id;

  public ResourceIdMatches(String id) {
    Preconditions.checkNotNull(id);
    this.id = id;
  }

  @Override
  public boolean apply(Resource concept) {
    return id.equals(concept.getId());
  }

}
