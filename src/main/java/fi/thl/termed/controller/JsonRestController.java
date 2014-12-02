package fi.thl.termed.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

import fi.thl.termed.service.JsonService;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
public class JsonRestController {

  private final JsonService service;

  @Autowired
  public JsonRestController(JsonService service) {
    this.service = service;
  }

  @RequestMapping(method = GET, value = "schemes")
  @ResponseBody
  public JsonArray querySchemes() {
    return service.querySchemes();
  }

  @RequestMapping(method = GET, value = "schemes/{schemeId}")
  @ResponseBody
  public JsonObject getScheme(@PathVariable("schemeId") String schemeId) {
    return service.getScheme(schemeId);
  }

  @RequestMapping(method = GET, value = "schemes/{schemeId}/collections")
  @ResponseBody
  public JsonArray queryCollections(@PathVariable("schemeId") String schemeId) {
    return service.queryCollections(schemeId);
  }

  @RequestMapping(method = GET, value = "schemes/{schemeId}/collections/{collectionId}")
  @ResponseBody
  public JsonObject getCollection(@PathVariable("collectionId") String collectionId) {
    return service.getCollection(collectionId);
  }

  @RequestMapping(method = GET, value = "concepts")
  @ResponseBody
  public JsonArray queryConceptsFromAllSchemes(
      @RequestParam(value = "query", required = false, defaultValue = "") String query,
      @RequestParam(value = "first", required = false, defaultValue = "0") int first,
      @RequestParam(value = "max", required = false, defaultValue = "50") int max,
      @RequestParam(value = "orderBy", required = false) List<String> orderBy) {
    return service.queryConcepts(query, first, max < 0 ? Integer.MAX_VALUE : max, orderBy);
  }

  @RequestMapping(method = GET, value = "schemes/{schemeId}/concepts")
  @ResponseBody
  public JsonArray queryConcepts(
      @PathVariable("schemeId") String schemeId,
      @RequestParam(value = "query", required = false, defaultValue = "") String query,
      @RequestParam(value = "first", required = false, defaultValue = "0") int first,
      @RequestParam(value = "max", required = false, defaultValue = "50") int max,
      @RequestParam(value = "orderBy", required = false) List<String> orderBy) {
    return service
        .queryConcepts(schemeId, query, first, max < 0 ? Integer.MAX_VALUE : max, orderBy);
  }

  @RequestMapping(method = GET, value = "schemes/{schemeId}/concepts/{conceptId}")
  @ResponseBody
  public JsonObject getConcept(@PathVariable("conceptId") String conceptId) {
    return service.getConcept(conceptId);
  }

  @RequestMapping(method = GET, value = "schemes/{schemeId}/concepts/{conceptId}/broader")
  @ResponseBody
  public JsonArray getConceptBroaderPaths(@PathVariable("conceptId") String conceptId) {
    return service.getConceptBroaderPaths(conceptId);
  }

  // modifiers

  @RequestMapping(method = POST, value = "schemes",
      consumes = "application/json;charset=UTF-8")
  @ResponseBody
  public JsonObject saveScheme(@RequestBody JsonObject data) {
    return service.saveScheme(data);
  }

  @RequestMapping(method = DELETE, value = "schemes/{id}")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void removeScheme(@PathVariable("id") String id) {
    service.removeScheme(id);
  }

  @RequestMapping(method = POST, value = "schemes/{schemeId}/collections",
      consumes = "application/json;charset=UTF-8")
  @ResponseBody
  public JsonObject saveCollection(@RequestBody JsonObject data) {
    return service.saveCollection(data);
  }

  @RequestMapping(method = DELETE, value = "schemes/{schemeId}/collections/{id}")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void removeCollection(@PathVariable("id") String id) {
    service.removeCollection(id);
  }

  @RequestMapping(method = POST, value = "schemes/{schemeId}/concepts",
      consumes = "application/json;charset=UTF-8")
  @ResponseBody
  public JsonElement saveConcept(@RequestBody JsonElement data) {
    return service.saveConcept(data);
  }

  @RequestMapping(method = DELETE, value = "schemes/{schemeId}/concepts/{id}")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void removeConcept(@PathVariable("id") String id) {
    service.removeConcept(id);
  }

}
