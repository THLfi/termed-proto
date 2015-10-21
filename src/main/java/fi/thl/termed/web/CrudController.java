package fi.thl.termed.web;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import fi.thl.termed.service.JsonCrudService;
import fi.thl.termed.util.LuceneQueryStringUtils;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = "/api/crud")
public class CrudController {

  private final JsonCrudService service;

  @Autowired
  public CrudController(JsonCrudService service) {
    this.service = service;
  }

  @RequestMapping(method = GET, value = "/{collection}")
  public JsonArray query(
      @PathVariable("collection") String collection,
      @RequestParam(value = "schemeId", required = false, defaultValue = "") String schemeId,
      @RequestParam(value = "query", required = false, defaultValue = "") String query,
      @RequestParam(value = "first", required = false, defaultValue = "0") Integer first,
      @RequestParam(value = "max", required = false, defaultValue = "50") Integer max,
      @RequestParam(value = "orderBy", required = false, defaultValue = "") List<String> orderBy,
      @RequestParam(value = "cached", required = false, defaultValue = "true") boolean cached) {
    String schemaQuery = addSchemeIdToQuery(schemeId, query);
    return cached ? service.queryCached(collection, schemaQuery, first, max, orderBy)
                  : service.query(collection, schemaQuery, first, max, orderBy);
  }

  private String addSchemeIdToQuery(String schemeId, String query) {
    return LuceneQueryStringUtils
        .and(LuceneQueryStringUtils.termQuery("scheme.id", schemeId), query);
  }

  @RequestMapping(method = GET, value = "/{collection}/{id}")
  public JsonObject get(@PathVariable("collection") String collection,
                        @PathVariable("id") String id) {
    return service.get(collection, id);
  }

  @RequestMapping(method = POST, value = "/{collection}",
      consumes = "application/json;charset=UTF-8")
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
