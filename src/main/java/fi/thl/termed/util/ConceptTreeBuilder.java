package fi.thl.termed.util;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fi.thl.termed.model.Concept;
import fi.thl.termed.model.PropertyValue;

public class ConceptTreeBuilder {

  private static class TreeNode {

    private String id;
    private String parent;
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

    public String getParent() {
      return parent;
    }

    public void setParent(String parent) {
      this.parent = parent;
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

    @SuppressWarnings("unchecked")
    public Set<TreeNode> getChildSet() {
      if (children != null && children instanceof Set) {
        return (Set<TreeNode>) children;
      }
      return Collections.emptySet();
    }

    @SuppressWarnings("unchecked")
    public void replaceChild(TreeNode child) {
      if (children != null && children instanceof Set) {
        Set<TreeNode> childSet = (Set<TreeNode>) children;
        childSet.remove(child);
        childSet.add(child);
      }
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

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      TreeNode that = (TreeNode) o;

      return Objects.equal(text, that.text);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(text);
    }

  }

  public static List<TreeNode> buildConceptTreesFor(Concept concept) {
    List<TreeNode> roots = Lists.newArrayList();

    TreeNode node = transformAndCollectRootNodes(concept, roots);
    node.setState(ImmutableMap.of("opened", true, "selected", true));
    node.setChildren(toTreeNode(node, concept.getNarrower()));

    return Lists.newArrayList(roots);
  }

  private static TreeNode transformAndCollectRootNodes(Concept concept, List<TreeNode> roots) {
    TreeNode node = toTreeNode(concept);
    node.setState(ImmutableMap.of("opened", true));

    if (concept.hasBroader()) {
      for (Concept broader : concept.getBroader()) {
        TreeNode broaderNode = transformAndCollectRootNodes(broader, roots);
        broaderNode.replaceChild(node);
        node.setId(broader.getId() + "." + node.getId());
      }
    } else {
      roots.add(node);
    }

    node.setChildren(toTreeNode(node, concept.getNarrower()));

    return node;
  }

  private static Set<TreeNode> toTreeNode(TreeNode broader, List<Concept> concepts) {
    Set<TreeNode> treeNodes = Sets.newHashSet();
    for (Concept concept : concepts) {
      TreeNode node = toTreeNode(concept);
      node.setId(broader.getId() + "." + node.getId());
      treeNodes.add(node);
    }
    return treeNodes;
  }

  private static TreeNode toTreeNode(Concept concept) {
    TreeNode node = new TreeNode();
    node.setId(concept.getId());
    node.setText(findProperty(concept, "prefLabel", "fi"));
    node.setIcon(false);
    if (concept.hasNarrower()) {
      node.setChildren(true);
    }
    String conceptUrl = "/schemes/" + concept.getScheme().getId() + "/concepts/" + concept.getId();
    node.setLinkElementAttributes(ImmutableMap.of("href", conceptUrl));
    node.setListElementAttributes(ImmutableMap.of("conceptId", concept.getId()));
    return node;
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
