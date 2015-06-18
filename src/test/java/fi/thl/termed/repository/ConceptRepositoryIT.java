package fi.thl.termed.repository;

import com.google.common.collect.Lists;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Autowired
  private SchemeRepository schemeRepository;

  @Autowired
  private ConceptRepository conceptRepository;

  @Test
  public void shouldReferenceConcept() {
    ConceptReferenceType broaderType = new ConceptReferenceType("broader");
    broaderType = em.merge(broaderType);

    Scheme exampleScheme = new Scheme("exampleScheme");
    exampleScheme = schemeRepository.saveAndFlush(exampleScheme);

    Concept broader = new Concept("exampleBroaderConcept");
    broader.setScheme(exampleScheme);
    broader = conceptRepository.saveAndFlush(broader);

    Concept narrower = new Concept("exampleNarrowerConcept");
    narrower.setScheme(exampleScheme);
    ConceptReference conceptReference = new ConceptReference(broaderType, narrower, broader);
    narrower.setReferences(Lists.newArrayList(conceptReference));
    em.persist(narrower);
    em.flush();

    assertNotNull(em.find(Concept.class, "exampleNarrowerConcept").getReferences());
  }

}
