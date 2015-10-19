package fi.thl.termed.domain;

import com.google.common.collect.Lists;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml",
                                   "classpath:spring/mvc-dispatcher-servlet.xml",
                                   "classpath:spring/spring-security.xml"})
@TransactionConfiguration
@Transactional
public class ConceptIT {

  @PersistenceContext
  private EntityManager em;

  @BeforeClass
  public static void setUpBeforeClass() {
    System.setProperty("propertyFilePath", "");
  }

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
    ConceptReference conceptReference = new ConceptReference(broaderType, narrower, broader);
    narrower.setReferences(Lists.newArrayList(conceptReference));
    em.persist(narrower);
    em.flush();

    assertNotNull(em.find(Concept.class, "exampleNarrowerConcept").getReferences());
  }

}
