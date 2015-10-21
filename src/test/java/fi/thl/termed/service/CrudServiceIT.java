package fi.thl.termed.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.thl.termed.Application;
import fi.thl.termed.domain.Concept;
import fi.thl.termed.domain.Scheme;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@IntegrationTest("server.port:0")
public class CrudServiceIT {

  @Autowired
  private CrudService crudService;

  @Test
  public void shouldSupportCrudOperationsForScheme() {
    String schemeId = "exampleScheme";
    assertNull(crudService.get(Scheme.class, schemeId));

    crudService.save(Scheme.class, new Scheme(schemeId));
    assertNotNull(crudService.get(Scheme.class, schemeId));

    crudService.remove(Scheme.class, schemeId);
    assertNull(crudService.get(Scheme.class, schemeId));
  }

  @Test
  public void shouldSupportCrudOperationsForConcept() {
    String schemeId = "exampleScheme";
    Scheme scheme = crudService.save(Scheme.class, new Scheme(schemeId));

    String conceptId = "exampleConcept";
    assertNull(crudService.get(Concept.class, conceptId));

    Concept concept = new Concept(conceptId);
    concept.setScheme(scheme);
    crudService.save(Concept.class, concept);
    assertNotNull(crudService.get(Concept.class, conceptId));

    crudService.remove(Concept.class, conceptId);
    assertNull(crudService.get(Concept.class, conceptId));

    crudService.remove(Scheme.class, schemeId);
  }

}
