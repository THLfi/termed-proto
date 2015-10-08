package fi.thl.termed.model;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

import fi.thl.termed.util.ListUtils;

public class LazyConceptTree extends SchemeResource {

  private Concept concept;
  private Set<String> path;
  private Function<Concept, List<Concept>> childFunction;

  private List<LazyConceptTree> children;

  public LazyConceptTree(Concept concept, Function<Concept, List<Concept>> childFunction) {
    this(concept, Sets.newLinkedHashSet(Lists.newArrayList(concept.getId())), childFunction);
  }

  public LazyConceptTree(Concept concept,
                         Set<String> path,
                         Function<Concept, List<Concept>> childFunction) {
    super(concept);
    this.concept = concept;
    this.path = path;
    this.childFunction = childFunction;
  }

  public Set<String> getPath() {
    return path;
  }

  public List<LazyConceptTree> getChildren() {
    if (children == null) {
      loadChildren();
    }
    return children;
  }

  // check path for loops and load children
  private void loadChildren() {
    List<LazyConceptTree> children = Lists.newArrayList();

    for (Concept child : ListUtils.nullToEmpty(childFunction.apply(concept))) {
      if (!path.contains(child.getId())) {
        Set<String> childPath = Sets.newLinkedHashSet(path);
        childPath.add(child.getId());

        children.add(new LazyConceptTree(child, childPath, childFunction));
      }
    }

    this.children = children;
  }

}
