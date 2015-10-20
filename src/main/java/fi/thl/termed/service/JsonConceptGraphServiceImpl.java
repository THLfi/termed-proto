package fi.thl.termed.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import fi.thl.termed.domain.Concept;
import fi.thl.termed.domain.JsTree;
import fi.thl.termed.domain.LazyConceptTree;
import fi.thl.termed.serializer.Converters;
import fi.thl.termed.util.ConceptGraphUtils;
import fi.thl.termed.util.GetResourceId;
import fi.thl.termed.util.ListUtils;
import fi.thl.termed.util.ToJsTreeFunction;

@Service
@Transactional(readOnly = true)
public class JsonConceptGraphServiceImpl implements JsonConceptGraphService {

  private ConceptGraphService conceptGraphService;

  private Gson fastGson;

  @Autowired
  public JsonConceptGraphServiceImpl(ConceptGraphService conceptGraphService) {
    this.conceptGraphService = conceptGraphService;

    GsonBuilder b = new GsonBuilder().setPrettyPrinting();
    Converters.registerDateConverter(b);
    Converters.registerPropertyListConverter(b);
    Converters.registerTruncatingConceptConverter(b);
    this.fastGson = b.create();
  }

  @Override
  public JsonArray getConceptPaths(String conceptId, final String referenceTypeId) {
    List<List<Concept>> paths =
        conceptGraphService.conceptPaths(conceptId, referenceTypeId);

    return fastGson.toJsonTree(paths).getAsJsonArray();
  }

  @Override
  public JsonArray getConceptJsTrees(String conceptId, String referenceTypeId) {
    List<List<Concept>> paths = conceptGraphService.conceptPaths(conceptId, referenceTypeId);

    List<String> roots = conceptIds(ConceptGraphUtils.findRoots(paths));
    List<String> opened = conceptIds(ListUtils.flatten(paths));

    List<LazyConceptTree> trees = conceptGraphService.toTrees(roots, referenceTypeId);

    List<JsTree> jsTrees = Lists.transform(trees, new ToJsTreeFunction(Sets.newHashSet(opened),
                                                                       conceptId));

    return fastGson.toJsonTree(jsTrees).getAsJsonArray();
  }

  private List<String> conceptIds(List<Concept> concepts) {
    return Lists.transform(concepts, new GetResourceId());
  }

  @Override
  public JsonArray getConceptTrees(String schemeId, String referenceTypeId) {
    return getConceptTrees(schemeId, referenceTypeId, null);
  }

  @Override
  public JsonArray getConceptTrees(String schemeId, String referenceTypeId, List<String> orderBy) {
    List<LazyConceptTree> trees = Lists.newArrayList();
    for (LazyConceptTree tree : conceptGraphService.roots(schemeId, referenceTypeId, orderBy)) {
      tree.recursiveLoadChildren();
      trees.add(tree);
    }
    return fastGson.toJsonTree(trees).getAsJsonArray();
  }

}
