package fi.thl.termed;

import com.google.common.collect.ImmutableMap;
import com.google.gson.GsonBuilder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

import java.util.Map;

import fi.thl.termed.domain.Collection;
import fi.thl.termed.domain.Concept;
import fi.thl.termed.domain.ConceptReferenceType;
import fi.thl.termed.domain.Property;
import fi.thl.termed.domain.Scheme;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(Application.class);
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public Map<String, Class> collectionClassMap() {
    return ImmutableMap.<String, Class>builder()
        .put("schemes", Scheme.class)
        .put("concepts", Concept.class)
        .put("collections", Collection.class)
        .put("properties", Property.class)
        .put("conceptReferenceTypes", ConceptReferenceType.class)
        .build();
  }

  @Bean
  public HttpMessageConverters messageConverters() {
    GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
    gsonHttpMessageConverter.setGson(new GsonBuilder().setPrettyPrinting().create());
    return new HttpMessageConverters(gsonHttpMessageConverter);
  }

}
