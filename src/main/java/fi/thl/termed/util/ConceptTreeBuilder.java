package fi.thl.termed.util;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.Set;

import fi.thl.termed.model.Concept;
import fi.thl.termed.model.PropertyValue;

public class ConceptTreeBuilder {

  private static class TreeNode {

    private String id;
    private String text;
    private Object icon;
    private Map<String, Boolean> state;
    private Object children;

    public TreeNode(String id, String text) {
      this.id = id;
      this.text = text;
    }

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

    @SuppressWarnings("unchecked")
    public void replaceChild(TreeNode child) {
      if (children != null && children instanceof Set) {
        Set<TreeNode> childSet = (Set<TreeNode>) children;
        childSet.remove(child);
        childSet.add(child);
      }
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

      return Objects.equal(id, that.id);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(id);
    }

  }

  public static List<TreeNode> buildConceptTreesFor(Concept concept) {
    List<TreeNode> roots = Lists.newArrayList();

    TreeNode node = truncateBroaderConcept(concept, roots);
    node.setState(ImmutableMap.of("opened", true, "selected", true));
    node.setChildren(toTreeNode(concept.getNarrower()));

    return roots;
  }

  private static TreeNode truncateBroaderConcept(Concept concept, List<TreeNode> roots) {
    TreeNode node = toTreeNode(concept);
    node.setState(ImmutableMap.of("opened", true));
    node.setChildren(toTreeNode(concept.getNarrower()));

    if (concept.hasBroader()) {
      for (Concept broader : concept.getBroader()) {
        TreeNode broaderNode = truncateBroaderConcept(broader, roots);
        broaderNode.replaceChild(node);
      }
    } else {
      roots.add(node);
    }

    return node;
  }

  private static TreeNode toTreeNode(Concept concept) {
    TreeNode node = new TreeNode(concept.getId(), findProperty(concept, "prefLabel", "fi"));
    node.setIcon(false);
    if (concept.hasNarrower()) {
      node.setChildren(true);
    }
    return node;
  }

  private static Set<TreeNode> toTreeNode(List<Concept> concepts) {
    Set<TreeNode> treeNodes = Sets.newHashSet();
    for (Concept concept : concepts) {
      treeNodes.add(toTreeNode(concept));
    }
    return treeNodes;
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
