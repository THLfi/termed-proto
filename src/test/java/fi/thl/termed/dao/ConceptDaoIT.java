package fi.thl.termed.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fi.thl.termed.model.Concept;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/applicationContext.xml",
                                   "file:src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml"})
@TransactionConfiguration
@Transactional
public class ConceptDaoIT {

  @Autowired
  private ConceptDao conceptDao;

  private Concept exampleConcept() {
    return new Concept("person");
  }

  private Concept exampleConceptWithProperties() {
    Concept person = exampleConcept();
    person.addProperty("prefLabel", "en", "Person");
    person.addProperty("prefLabel", "fi", "Henkilö");
    return person;
  }

  @Test
  public void shouldSaveConcept() {
    Concept concept = exampleConcept();

    assertNotEquals(concept, conceptDao.get(concept.getId()));

    conceptDao.save(concept);
    assertEquals(concept, conceptDao.get(concept.getId()));
  }

  @Test
  public void shouldDeleteConcept() {
    Concept concept = exampleConcept();

    assertNotEquals(concept, conceptDao.get(concept.getId()));

    conceptDao.save(concept);
    assertEquals(concept, conceptDao.get(concept.getId()));

    conceptDao.remove(concept.getId());
    assertNull(conceptDao.get(concept.getId()));
  }

  @Test
  public void shouldSaveConceptWithProperties() {
    Concept concept = exampleConceptWithProperties();

    conceptDao.save(concept);
    assertEquals(concept, conceptDao.get(concept.getId()));

    concept.setProperties(null);
    assertNotEquals(concept, conceptDao.get(concept.getId()));
  }

  @Test
  public void shouldDeleteConceptWithProperties() {
    Concept concept = exampleConceptWithProperties();

    conceptDao.save(concept);
    assertEquals(concept, conceptDao.get(concept.getId()));

    conceptDao.remove(concept.getId());
    assertNull(conceptDao.get(concept.getId()));
  }

  @Test
  public void shouldSaveConceptPropertyValueInsertions() {
    Concept concept = exampleConceptWithProperties();

    conceptDao.save(concept);
    assertEquals(concept, conceptDao.get(concept.getId()));

    concept.addProperty("note", "en", "A New Property Value");
    assertNotEquals(concept, conceptDao.get(concept.getId()));

    conceptDao.save(concept);
    assertEquals(concept, conceptDao.get(concept.getId()));
  }

  @Test
  public void shouldSaveConceptPropertyValueModifications() {
    Concept concept = exampleConceptWithProperties();

    conceptDao.save(concept);
    assertEquals(concept, conceptDao.get(concept.getId()));

    concept.addProperty("prefLabel", "en", "Edited Existing Concept Label");
    assertNotEquals(concept, conceptDao.get(concept.getId()));

    conceptDao.save(concept);
    assertEquals(concept, conceptDao.get(concept.getId()));
  }

  @Test
  public void shouldSaveConceptPropertyValueRemoval() {
    Concept concept = exampleConceptWithProperties();

    conceptDao.save(concept);
    assertEquals(concept, conceptDao.get(concept.getId()));

    concept.getProperties().clear();
    assertNotEquals(concept, conceptDao.get(concept.getId()));

    conceptDao.save(concept);
    assertEquals(concept, conceptDao.get(concept.getId()));
  }

}
