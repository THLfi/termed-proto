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
import fi.thl.termed.model.ConceptReference;
import fi.thl.termed.model.ConceptReferenceType;
import fi.thl.termed.model.Scheme;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/applicationContext.xml",
                                   "file:src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml",
                                   "file:src/main/webapp/WEB-INF/spring-security.xml"})
@TransactionConfiguration
@Transactional
public class CollectionReferenceRepositoryIT {

  @PersistenceContext
  private EntityManager em;

  @Test
  public void shouldSaveConceptReferences() {
    Scheme exampleScheme = new Scheme("exampleScheme");
    exampleScheme = em.merge(exampleScheme);

    Concept concept1 = new Concept("exampleConcept1");
    concept1.setScheme(exampleScheme);
    concept1 = em.merge(concept1);

    Concept concept2 = new Concept("exampleConcept2");
    concept2.setScheme(exampleScheme);
    concept2 = em.merge(concept2);

    Concept concept3 = new Concept("exampleConcept3");
    concept3.setScheme(exampleScheme);
    concept3 = em.merge(concept3);

    ConceptReferenceType broader = new ConceptReferenceType("broader");
    broader = em.merge(broader);

    em.merge(new ConceptReference(broader, concept1, concept2));
    em.merge(new ConceptReference(broader, concept1, concept3));

    em.flush();
  }

}
