package fi.thl.termed.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fi.thl.termed.model.AuditedResource;
import fi.thl.termed.model.Concept;
import fi.thl.termed.repository.ConceptRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/applicationContext.xml",
                                   "file:src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml",
                                   "file:src/main/webapp/WEB-INF/spring-security.xml"})
@TransactionConfiguration
@Transactional
public class ConceptRepositoryIT {

  @Autowired
  private ConceptRepository conceptRepository;

  private Concept exampleConcept() {
    return new Concept("person");
  }

  private Concept exampleConceptWithProperties() {
    Concept person = exampleConcept();
    person.addProperty("prefLabel", "en", "Person");
    person.addProperty("prefLabel", "fi", "Henkilö");
    return person;
  }

  private void assertConceptEqualsIgnoreAuditData(Concept expected, Concept actual) {
    assertEquals(nonAuditedCopy(expected), nonAuditedCopy(actual));
  }

  private void assertConceptNotEqualsIgnoreAuditData(Concept expected, Concept actual) {
    assertNotEquals(nonAuditedCopy(expected), nonAuditedCopy(actual));
  }

  private Concept nonAuditedCopy(Concept concept) {
    return concept != null ? clearAuditData(new Concept(concept)) : null;
  }

  private <T extends AuditedResource> T clearAuditData(T auditedResource) {
    auditedResource.setCreatedBy(null);
    auditedResource.setLastModifiedBy(null);
    auditedResource.setCreatedDate(null);
    auditedResource.setLastModifiedDate(null);
    return auditedResource;
  }

  @Test
  public void shouldSaveConcept() {
    Concept concept = exampleConcept();
    conceptRepository.saveConcept(concept);
    assertConceptEqualsIgnoreAuditData(concept, conceptRepository.findOne(concept.getId()));
  }

  @Test
  public void shouldDeleteConcept() {
    Concept concept = exampleConcept();

    conceptRepository.saveConcept(concept);
    assertConceptEqualsIgnoreAuditData(concept, conceptRepository.findOne(concept.getId()));

    conceptRepository.delete(concept.getId());
    assertNull(conceptRepository.findOne(concept.getId()));
  }

  @Test
  public void shouldSaveConceptWithProperties() {
    Concept concept = exampleConceptWithProperties();

    conceptRepository.saveConcept(concept);
    assertConceptEqualsIgnoreAuditData(concept, conceptRepository.findOne(concept.getId()));

    concept.setProperties(null);
    assertConceptNotEqualsIgnoreAuditData(concept, conceptRepository.findOne(concept.getId()));
  }

  @Test
  public void shouldDeleteConceptWithProperties() {
    Concept concept = exampleConceptWithProperties();

    conceptRepository.saveConcept(concept);
    assertConceptEqualsIgnoreAuditData(concept, conceptRepository.findOne(concept.getId()));

    conceptRepository.delete(concept.getId());
    assertNull(conceptRepository.findOne(concept.getId()));
  }

  @Test
  public void shouldSaveConceptPropertyValueInsertions() {
    Concept concept = exampleConceptWithProperties();

    conceptRepository.saveConcept(concept);
    assertConceptEqualsIgnoreAuditData(concept, conceptRepository.findOne(concept.getId()));

    concept.addProperty("note", "en", "A New Property Value");
    assertConceptNotEqualsIgnoreAuditData(concept, conceptRepository.findOne(concept.getId()));

    conceptRepository.saveConcept(concept);
    assertConceptEqualsIgnoreAuditData(concept, conceptRepository.findOne(concept.getId()));
  }

  @Test
  public void shouldSaveConceptPropertyValueModifications() {
    Concept concept = exampleConceptWithProperties();

    conceptRepository.saveConcept(concept);
    assertConceptEqualsIgnoreAuditData(concept, conceptRepository.findOne(concept.getId()));

    concept.addProperty("prefLabel", "en", "Edited Existing Concept Label");
    assertConceptNotEqualsIgnoreAuditData(concept, conceptRepository.findOne(concept.getId()));

    conceptRepository.saveConcept(concept);
    assertConceptEqualsIgnoreAuditData(concept, conceptRepository.findOne(concept.getId()));
  }

  @Test
  public void shouldSaveConceptPropertyValueRemoval() {
    Concept concept = exampleConceptWithProperties();

    conceptRepository.saveConcept(concept);
    assertConceptEqualsIgnoreAuditData(concept, conceptRepository.findOne(concept.getId()));

    concept.getProperties().clear();
    assertConceptNotEqualsIgnoreAuditData(concept, conceptRepository.findOne(concept.getId()));

    conceptRepository.saveConcept(concept);
    assertConceptEqualsIgnoreAuditData(concept, conceptRepository.findOne(concept.getId()));
  }

}
