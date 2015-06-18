package fi.thl.termed.util;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

import fi.thl.termed.model.Concept;
import fi.thl.termed.model.PropertyValue;

public class JsTreeBuilder {

  public static List<JsTreeNode> buildTreesFor(Concept concept) {
    if (hasParts(concept)) {
      return buildTreesFor(concept,
                           ConceptReferenceFunctions.getPartsFunction,
                           ConceptReferenceFunctions.getPartOfFunction);
    }
    return buildTreesFor(concept,
                         ConceptReferenceFunctions.getNarrowerFunction,
                         ConceptReferenceFunctions.getBroaderFunction);
  }

  private static boolean hasParts(Concept concept) {
    List<Concept> parts = ConceptReferenceFunctions.getPartsFunction.apply(concept);
    List<Concept> partOf = ConceptReferenceFunctions.getPartOfFunction.apply(concept);

    return !ListUtils.nullToEmpty(parts).isEmpty() || !ListUtils.nullToEmpty(partOf).isEmpty();
  }

  public static List<JsTreeNode> buildTreesFor(Concept concept,
                                               Function<Concept, List<Concept>> getNeighbour,
                                               Function<Concept, List<Concept>> getNeighbourInverse) {

    List<List<Concept>> paths = ConceptGraphUtils.collectPaths(concept, getNeighbourInverse);

    List<Concept> roots = ConceptGraphUtils.findRoots(paths);
    Set<Concept> opened = Sets.newHashSet(ListUtils.flatten(paths));

    return toJsTreeNode(roots, getNeighbour, opened, concept);
  }

  private static List<JsTreeNode> toJsTreeNode(List<Concept> concepts,
                                               Function<Concept, List<Concept>> getNeighbours,
                                               Set<Concept> opened, Concept selected) {
    List<JsTreeNode> jsTreeNodes = Lists.newArrayList();
    for (Concept concept : concepts) {
      jsTreeNodes.add(toJsTreeNode(concept, getNeighbours, opened, selected));
    }
    return jsTreeNodes;
  }

  private static JsTreeNode toJsTreeNode(Concept concept,
                                         Function<Concept, List<Concept>> getNeighbours,
                                         Set<Concept> opened, Concept selected) {
    JsTreeNode jsTreeNode = new JsTreeNode();
    jsTreeNode.setId(pathId(concept, getNeighbours));
    jsTreeNode.setText(findProperty(concept, "prefLabel", "fi") + smallMuted(localName(concept)));
    jsTreeNode.setState(
        ImmutableMap.of("opened", opened.contains(concept), "selected", concept.equals(selected)));
    jsTreeNode.setIcon(false);
    String conceptUrl = "/schemes/" + concept.getScheme().getId() + "/concepts/" + concept.getId();
    jsTreeNode.setLinkElementAttributes(ImmutableMap.of("href", conceptUrl));
    jsTreeNode.setListElementAttributes(ImmutableMap.of("conceptId", concept.getId(), "index",
                                                        findProperty(concept, "index", "fi")));
    List<Concept> neighbours = ListUtils.nullToEmpty(getNeighbours.apply(concept));

    if (neighbours.isEmpty()) {
      jsTreeNode.setChildren(false);
    } else if (opened.contains(concept)) {
      jsTreeNode.setChildren(toJsTreeNode(neighbours, getNeighbours, opened, selected));
    } else {
      jsTreeNode.setChildren(true);
    }

    return jsTreeNode;
  }

  private static String smallMuted(String text) {
    return " <small class='text-muted'>" + text + "</small>";
  }

  private static String localName(Concept concept) {
    return concept.hasUri() ? localName(concept.getUri()) : "";
  }

  private static String localName(String uri) {
    int i = uri.lastIndexOf("#");
    i = i == -1 ? uri.lastIndexOf("/") : -1;
    return uri.substring(i + 1);
  }

  private static String pathId(Concept concept, Function<Concept, List<Concept>> getNeighbours) {
    return DigestUtils.sha1Hex(Joiner.on('.').join(Lists.transform(
        ListUtils.flatten(ConceptGraphUtils.collectPaths(concept, getNeighbours)),
        new GetResourceId())));
  }

  private static String findProperty(Concept concept, String propertyId, String lang) {
    for (PropertyValue property : concept.getProperties()) {
      if (propertyId.equals(property.getPropertyId()) && lang.equals(property.getLang())) {
        return property.getValue();
      }
    }
    return "";
  }

  private static class JsTreeNode {

    private String id;
    private String text;
    private Object icon;
    private Map<String, Boolean> state;
    private Object children;
    @SerializedName("a_attr")
    private Map<String, String> linkElementAttributes;
    @SerializedName("li_attr")
    private Map<String, String> listElementAttributes;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getText() {
      return text;
    }

    public void setText(String text) {
      this.text = text;
    }

    public Object getIcon() {
      return icon;
    }

    public void setIcon(Object icon) {
      this.icon = icon;
    }

    public Map<String, Boolean> getState() {
      return state;
    }

    public void setState(Map<String, Boolean> state) {
      this.state = state;
    }

    public Object getChildren() {
      return children;
    }

    public void setChildren(Object children) {
      this.children = children;
    }

    public Map<String, String> getLinkElementAttributes() {
      return linkElementAttributes;
    }

    public void setLinkElementAttributes(Map<String, String> linkElementAttributes) {
      this.linkElementAttributes = linkElementAttributes;
    }

    public Map<String, String> getListElementAttributes() {
      return listElementAttributes;
    }

    public void setListElementAttributes(Map<String, String> listElementAttributes) {
      this.listElementAttributes = listElementAttributes;
    }
  }

}
