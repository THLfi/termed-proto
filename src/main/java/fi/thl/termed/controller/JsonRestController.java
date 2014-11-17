package fi.thl.termed.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
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

  @RequestMapping(method = GET, value = "concepts")
  @ResponseBody
  public JsonArray query(
      @RequestParam(value = "query", required = false, defaultValue = "") String query,
      @RequestParam(value = "max", required = false, defaultValue = "100") int max) {
    return query.isEmpty() ? service.queryConcepts(max) : service.queryConcepts(query, max);
  }

  @RequestMapping(method = GET, value = "concepts/{id}")
  @ResponseBody
  public JsonObject get(@PathVariable("id") String id) {
    return service.getConcept(id);
  }

  @RequestMapping(method = POST, value = "concepts", consumes = "application/json;charset=UTF-8")
  @ResponseBody
  public JsonElement save(@RequestBody JsonElement data) {
    if (data.isJsonObject()) {
      return service.saveConcept(data.getAsJsonObject());
    }
    if (data.isJsonArray()) {
      return service.saveAllConcepts(data.getAsJsonArray());
    }
    return JsonNull.INSTANCE;
  }

  @RequestMapping(method = DELETE, value = "concepts/{id}")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void remove(@PathVariable("id") String id) {
    service.removeConcept(id);
  }

}
