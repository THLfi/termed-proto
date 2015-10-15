package fi.thl.termed.controller;

import com.google.gson.JsonArray;
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

import fi.thl.termed.service.JsonCrudService;
import fi.thl.termed.util.LuceneQueryStringUtils;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping(value = "/crud", produces = "application/json;charset=UTF-8")
public class JsonCrudController {

  private final JsonCrudService service;

  @Autowired
  public JsonCrudController(JsonCrudService service) {
    this.service = service;
  }

  @RequestMapping(method = GET, value = "/{collection}")
  @ResponseBody
  public JsonArray query(
      @PathVariable("collection") String collection,
      @RequestParam(value = "schemeId", required = false, defaultValue = "") String schemeId,
      @RequestParam(value = "query", required = false, defaultValue = "") String query,
      @RequestParam(value = "first", required = false, defaultValue = "0") Integer first,
      @RequestParam(value = "max", required = false, defaultValue = "50") Integer max,
      @RequestParam(value = "orderBy", required = false, defaultValue = "") List<String> orderBy) {
    return service.query(collection, addSchemeIdToQuery(schemeId, query), first,
                         max < 0 ? Integer.MAX_VALUE : max, orderBy);
  }

  private String addSchemeIdToQuery(String schemeId, String query) {
    return LuceneQueryStringUtils
        .and(LuceneQueryStringUtils.termQuery("scheme.id", schemeId), query);
  }

  @RequestMapping(method = GET, value = "/{collection}/{id}")
  @ResponseBody
  public JsonObject get(@PathVariable("collection") String collection,
                        @PathVariable("id") String id) {
    return service.get(collection, id);
  }

  @RequestMapping(method = POST, value = "/{collection}",
      consumes = "application/json;charset=UTF-8")
  @ResponseBody
  public JsonObject save(@PathVariable("collection") String collection,
                         @RequestBody JsonObject data) {
    return service.save(collection, data);
  }

  @RequestMapping(method = DELETE, value = "/{collection}/{id}")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void remove(@PathVariable("collection") String collection,
                     @PathVariable("id") String id) {
    service.remove(collection, id);
  }

}
