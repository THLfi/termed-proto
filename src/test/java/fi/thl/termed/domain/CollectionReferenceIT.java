package fi.thl.termed.domain;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml",
                                   "classpath:spring/mvc-dispatcher-servlet.xml",
                                   "classpath:spring/spring-security.xml"})
@TransactionConfiguration
@Transactional
public class CollectionReferenceIT {

  @PersistenceContext
  private EntityManager em;

  @BeforeClass
  public static void setUpBeforeClass() {
    System.setProperty("propertyFilePath", "");
  }

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
