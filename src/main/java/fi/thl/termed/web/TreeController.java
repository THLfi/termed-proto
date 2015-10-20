package fi.thl.termed.web;

import com.google.gson.JsonArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import fi.thl.termed.service.JsonConceptGraphService;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/api")
public class TreeController {

  @SuppressWarnings("all")
  private Logger log = LoggerFactory.getLogger(getClass());

  private final JsonConceptGraphService jsonConceptGraphService;

  @Autowired
  public TreeController(JsonConceptGraphService jsonConceptGraphService) {
    this.jsonConceptGraphService = jsonConceptGraphService;
  }

  @RequestMapping(method = GET, value = "/trees/concepts/{conceptId}")
  public JsonArray getConceptJsTrees(@PathVariable("conceptId") String conceptId) {
    return jsonConceptGraphService.getConceptJsTrees(conceptId, "broader");
  }

  @RequestMapping(method = GET, value = "/paths/concepts/{conceptId}/{referenceTypeId}")
  public JsonArray getConceptPaths(@PathVariable("conceptId") String conceptId,
                                   @PathVariable("referenceTypeId") String referenceTypeId) {
    return jsonConceptGraphService.getConceptPaths(conceptId, referenceTypeId);
  }

  @RequestMapping(method = GET, value = "/trees/schemes/{schemeId}/{referenceTypeId}")
  public JsonArray getConceptTrees(@PathVariable("schemeId") String schemeId,
                                   @PathVariable("referenceTypeId") String referenceTypeId,
                                   @RequestParam(value = "orderBy", required = false, defaultValue = "") List<String> orderBy) {
    return jsonConceptGraphService.getConceptTrees(schemeId, referenceTypeId, orderBy);
  }

}
