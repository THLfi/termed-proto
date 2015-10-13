package fi.thl.termed.util;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.List;
import java.util.Set;

import fi.thl.termed.model.JsTree;
import fi.thl.termed.model.LazyConceptTree;
import fi.thl.termed.model.UriResource;

public class ToJsTreeFunction implements Function<LazyConceptTree, JsTree> {

  private Set<String> openedIds;
  private String selectedId;

  public ToJsTreeFunction(Set<String> openedIds, String selectedId) {
    this.openedIds = openedIds;
    this.selectedId = selectedId;
  }

  private static String smallMuted(String text) {
    return " <small class='text-muted'>" + text + "</small>";
  }

  private static String localName(UriResource concept) {
    return concept.hasUri() ? localName(concept.getUri()) : "";
  }

  private static String localName(String uri) {
    int i = uri.lastIndexOf("#");
    i = i == -1 ? uri.lastIndexOf("/") : -1;
    return uri.substring(i + 1);
  }

  @Override
  public JsTree apply(LazyConceptTree concept) {
    JsTree jsTree = new JsTree();

    jsTree.setId(DigestUtils.sha1Hex(Joiner.on('.').join(concept.getPath())));
    jsTree.setIcon(false);
    jsTree.setText(concept.getPropertyValue("prefLabel", "fi") +
                   smallMuted(localName(concept)));

    jsTree.setState(ImmutableMap.of("opened", openedIds.contains(concept.getId()),
                                    "selected", selectedId.equals(concept.getId())));

    String conceptUrl = "/schemes/" + concept.getScheme().getId() +
                        "/concepts/" + concept.getId();

    jsTree.setLinkElementAttributes(ImmutableMap.of("href", conceptUrl));
    jsTree.setListElementAttributes(ImmutableMap.of("conceptId", concept.getId(),
                                                    "index",
                                                    concept.getPropertyValue("index", "fi")));

    List<LazyConceptTree> children = concept.getChildren();

    if (children.isEmpty()) {
      jsTree.setChildren(false);
    } else if (openedIds.contains(concept.getId())) {
      jsTree.setChildren(Lists.transform(children, this));
    } else {
      jsTree.setChildren(true);
    }

    return jsTree;
  }
}
