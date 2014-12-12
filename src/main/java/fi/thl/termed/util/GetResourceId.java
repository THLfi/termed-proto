package fi.thl.termed.util;

import com.google.common.base.Function;

import fi.thl.termed.model.Resource;

public class GetResourceId implements Function<Resource, String> {

  @Override
  public String apply(Resource input) {
    return input.getId();
  }

}
