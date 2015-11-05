package fi.thl.termed.util;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import java.io.IOException;

public final class IOUtils {

  private IOUtils() {
  }

  public static String resourceToString(String name) throws IOException {
    return Resources.toString(Resources.getResource(name), Charsets.UTF_8);
  }

}
