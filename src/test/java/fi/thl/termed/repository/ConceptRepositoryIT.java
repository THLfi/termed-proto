package fi.thl.termed.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import fi.thl.termed.model.Concept;
import fi.thl.termed.model.ConceptReferenceType;
import fi.thl.termed.model.Scheme;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/applicationContext.xml",
                                   "file:src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml",
                                   "file:src/main/webapp/WEB-INF/spring-security.xml"})
@TransactionConfiguration
@Transactional
public class ConceptRepositoryIT {

  @PersistenceContext
  private EntityManager em;

  @Test
  public void shouldReferenceConcept() {
    ConceptReferenceType broaderType = new ConceptReferenceType("broader");
    broaderType = em.merge(broaderType);

    Scheme exampleScheme = new Scheme("exampleScheme");
    exampleScheme = em.merge(exampleScheme);

    Concept broader = new Concept("exampleBroaderConcept");
    broader.setScheme(exampleScheme);
    broader = em.merge(broader);

    Concept narrower = new Concept("exampleNarrowerConcept");
    narrower.setScheme(exampleScheme);
    narrower.addReferences(broaderType, broader);
    em.merge(narrower);
    em.flush();

    assertNotNull(em.find(Concept.class, "exampleNarrowerConcept").getReferences());
  }

}
