package fi.thl.termed.web;

import com.jayway.restassured.RestAssured;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import fi.thl.termed.Application;
import fi.thl.termed.domain.Concept;
import fi.thl.termed.domain.Scheme;
import fi.thl.termed.service.CrudService;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class CrudControllerIT {

  @Autowired
  private CrudService crudService;

  @Value("${local.server.port}")
  private int serverPort;

  @Before
  public void setUp() {
    Scheme scheme = new Scheme("exampleScheme");
    scheme = crudService.save(Scheme.class, scheme);

    Concept concept = new Concept("exampleConcept");
    concept.setScheme(scheme);
    concept.addProperty("prefLabel", "en", "Example Concept");
    crudService.save(Concept.class, concept);

    RestAssured.port = serverPort;
  }

  @Test
  public void shouldGetExampleConcept() {
    given()
        .auth()
        .basic("admin", "admin")
        .when()
        .get("/api/crud/concepts/exampleConcept")
        .then()
        .statusCode(HttpStatus.SC_OK)
        .body("id", is("exampleConcept"))
        .body("properties.prefLabel.en[0]", is("Example Concept"));
  }

}
