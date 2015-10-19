package fi.thl.termed.web.legacy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import fi.thl.termed.serializer.Converters;
import fi.thl.termed.service.JsonCrudService;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class JsonExporter {

  @SuppressWarnings("all")
  private Logger log = LoggerFactory.getLogger(getClass());

  private final JsonCrudService jsonService;

  private Gson gson;

  @Autowired
  public JsonExporter(JsonCrudService jsonService) {
    this.jsonService = jsonService;

    GsonBuilder b = new GsonBuilder().setPrettyPrinting();
    Converters.registerDateConverter(b);
    Converters.registerPropertyListConverter(b);
    Converters.registerConceptNarrowerTreeConverter(b);
    this.gson = b.create();
  }

  @RequestMapping(method = GET, value = "/api/export/{schemeId}/json", produces = "application/json;charset=UTF-8")
  public JsonArray exportJson(@PathVariable("schemeId") String schemeId)
      throws IOException {
    log.info("Exporting {}", schemeId);
    return jsonService
        .query("concepts", "+scheme.id:" + schemeId + " -broader.id:[* TO *]", 0, -1, null, gson);
  }

}
