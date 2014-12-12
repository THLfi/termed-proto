package fi.thl.termed.util;

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

    public void setIcon(Object icon) {
      this.icon = icon;
    }

    public Object getIcon() {
      return icon;
    }

    public Map<String, Boolean> getState() {
      return state;
    }

    public void setState(Map<String, Boolean> state) {
      this.state = state;
    }

    public void setChildren(Object children) {
      this.children = children;
    }

    public Object getChildren() {
      return children;
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

  public static List<JsTreeNode> buildTreesFor(Concept concept) {
    List<JsTreeNode> trees = Lists.newArrayList();

    Set<Concept> opened =
        Sets.newHashSet(ListUtils.flatten(ConceptGraphUtils.findBroaderPaths(concept)));

    for (Concept tree : ConceptGraphUtils.broaderTrees(concept)) {
      trees.add(toJsTreeNode(tree, opened, concept));
    }

    return trees;
  }

  private static JsTreeNode toJsTreeNode(Concept concept, Set<Concept> opened, Concept selected) {
    JsTreeNode jsTreeNode = new JsTreeNode();
    jsTreeNode.setId(pathId(concept));
    jsTreeNode.setText(findProperty(concept, "prefLabel", "fi"));
    jsTreeNode.setState(
        ImmutableMap.of("opened", opened.contains(concept), "selected", concept.equals(selected)));
    jsTreeNode.setIcon(false);
    String conceptUrl = "/schemes/" + concept.getScheme().getId() + "/concepts/" + concept.getId();
    jsTreeNode.setLinkElementAttributes(ImmutableMap.of("href", conceptUrl));
    jsTreeNode.setListElementAttributes(ImmutableMap.of("conceptId", concept.getId()));
    List<Concept> narrower = concept.getNarrower();
    if (narrower != null) {
      if (narrower.isEmpty()) {
        jsTreeNode.setChildren(true);
      } else {
        jsTreeNode.setChildren(toJsTreeNode(narrower, opened, selected));
      }
    }
    return jsTreeNode;
  }

  private static List<JsTreeNode> toJsTreeNode(List<Concept> concepts, Set<Concept> opened,
                                               Concept selected) {
    List<JsTreeNode> jsTreeNodes = Lists.newArrayList();
    for (Concept concept : concepts) {
      jsTreeNodes.add(toJsTreeNode(concept, opened, selected));
    }
    return jsTreeNodes;
  }

  private static String pathId(Concept concept) {
    return DigestUtils.sha1Hex(Joiner.on('.').join(Lists.transform(
        ListUtils.flatten(ConceptGraphUtils.findBroaderPaths(concept)), new GetResourceId())));
  }

  private static String findProperty(Concept concept, String propertyId, String lang) {
    for (PropertyValue property : concept.getProperties()) {
      if (propertyId.equals(property.getPropertyId()) && lang.equals(property.getLang())) {
        return property.getValue();
      }
    }
    return "";
  }

}
