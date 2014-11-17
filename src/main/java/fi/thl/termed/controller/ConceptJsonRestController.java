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

import java.util.List;

import fi.thl.termed.service.ConceptJsonService;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
public class ConceptJsonRestController {

  private final ConceptJsonService service;

  @Autowired
  public ConceptJsonRestController(ConceptJsonService service) {
    this.service = service;
  }

  @RequestMapping(method = GET, value = "concepts")
  @ResponseBody
  public JsonArray query(
      @RequestParam(value = "query", required = false, defaultValue = "") String query,
      @RequestParam(value = "first", required = false, defaultValue = "0") int first,
      @RequestParam(value = "max", required = false, defaultValue = "-1") int max,
      @RequestParam(value = "order", required = false, defaultValue = "") List<String> order) {
    return service.query(query, first, max, order);
  }

  @RequestMapping(method = GET, value = "concepts/{id}")
  @ResponseBody
  public JsonObject get(@PathVariable("id") String id) {
    return service.get(id);
  }

  @RequestMapping(method = POST, value = "concepts", consumes = "application/json;charset=UTF-8")
  @ResponseBody
  public JsonElement save(@RequestBody JsonElement data) {
    if (data.isJsonObject()) {
      return service.save(data.getAsJsonObject());
    }
    if (data.isJsonArray()) {
      return service.saveAll(data.getAsJsonArray());
    }
    return JsonNull.INSTANCE;
  }

  @RequestMapping(method = DELETE, value = "concepts/{id}")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void remove(@PathVariable("id") String id) {
    service.remove(id);
  }

}