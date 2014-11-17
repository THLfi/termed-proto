package fi.thl.termed.util;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public final class SKOS {

  private SKOS() {
  }

  public static final Resource Concept = skosResource("Concept");

  public static final Property prefLabel = skosProperty("prefLabel");
  public static final Property altLabel = skosProperty("altLabel");
  public static final Property hiddenLabel = skosProperty("hiddenLabel");
  public static final Property broader = skosProperty("broader");
  public static final Property narrower = skosProperty("narrower");
  public static final Property related = skosProperty("related");

  private static Property skosProperty(String localName) {
    return ResourceFactory.createProperty(skos(localName));
  }

  private static Resource skosResource(String localName) {
    return ResourceFactory.createResource(skos(localName));
  }

  private static String skos(String localName) {
    return "http://www.w3.org/2004/02/skos/core#" + localName;
  }

}
