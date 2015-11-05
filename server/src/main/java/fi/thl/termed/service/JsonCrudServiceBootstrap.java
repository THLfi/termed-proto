package fi.thl.termed.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

import javax.annotation.PostConstruct;

import fi.thl.termed.util.IOUtils;

/**
 * Populate default properties and reference types from files.
 */
@Component
public class JsonCrudServiceBootstrap {

  @Autowired
  private JsonCrudService jsonCrudService;

  @PostConstruct
  public void init() throws IOException {
    jsonCrudService.save("properties", resourceToJsonArray("data/properties.json"));
    jsonCrudService.save("referenceTypes", resourceToJsonArray("data/referenceTypes.json"));
  }

  private JsonArray resourceToJsonArray(String name) throws IOException {
    return new JsonParser().parse(IOUtils.resourceToString(name)).getAsJsonArray();
  }

}
