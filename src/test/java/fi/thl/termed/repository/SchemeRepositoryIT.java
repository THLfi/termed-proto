package fi.thl.termed.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fi.thl.termed.model.AuditedResource;
import fi.thl.termed.model.Scheme;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/applicationContext.xml",
                                   "file:src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml",
                                   "file:src/main/webapp/WEB-INF/spring-security.xml"})
@TransactionConfiguration
@Transactional
public class SchemeRepositoryIT {

  @Autowired
  private SchemeRepository schemeRepository;

  private Scheme exampleScheme() {
    return new Scheme("exampleScheme");
  }

  private Scheme exampleSchemeWithProperties() {
    Scheme person = exampleScheme();
    person.addProperty("prefLabel", "en", "Example Scheme");
    person.addProperty("prefLabel", "fi", "Esimerkkisanasto");
    return person;
  }

  private void assertSchemeEqualsIgnoreAuditData(Scheme expected, Scheme actual) {
    assertEquals(nonAuditedCopy(expected), nonAuditedCopy(actual));
  }

  private void assertSchemeNotEqualsIgnoreAuditData(Scheme expected, Scheme actual) {
    assertNotEquals(nonAuditedCopy(expected), nonAuditedCopy(actual));
  }

  private Scheme nonAuditedCopy(Scheme scheme) {
    return scheme != null ? clearAuditData(new Scheme(scheme)) : null;
  }

  private <T extends AuditedResource> T clearAuditData(T auditedResource) {
    auditedResource.setCreatedBy(null);
    auditedResource.setLastModifiedBy(null);
    auditedResource.setCreatedDate(null);
    auditedResource.setLastModifiedDate(null);
    return auditedResource;
  }

  @Test
  public void shouldSaveScheme() {
    Scheme scheme = exampleScheme();
    schemeRepository.saveAndFlush(scheme);
    assertSchemeEqualsIgnoreAuditData(scheme, schemeRepository.findOne(scheme.getId()));
  }

  @Test
  public void shouldDeleteScheme() {
    Scheme scheme = exampleScheme();

    schemeRepository.saveAndFlush(scheme);
    assertSchemeEqualsIgnoreAuditData(scheme, schemeRepository.findOne(scheme.getId()));

    schemeRepository.delete(scheme.getId());
    schemeRepository.flush();

    assertNull(schemeRepository.findOne(scheme.getId()));
  }

  @Test
  public void shouldSaveSchemeWithProperties() {
    Scheme scheme = exampleSchemeWithProperties();

    schemeRepository.saveAndFlush(scheme);
    assertSchemeEqualsIgnoreAuditData(scheme, schemeRepository.findOne(scheme.getId()));

    scheme.setProperties(null);
    assertSchemeNotEqualsIgnoreAuditData(scheme, schemeRepository.findOne(scheme.getId()));
  }

  @Test
  public void shouldDeleteSchemeWithProperties() {
    Scheme scheme = exampleSchemeWithProperties();

    schemeRepository.saveAndFlush(scheme);
    assertSchemeEqualsIgnoreAuditData(scheme, schemeRepository.findOne(scheme.getId()));

    schemeRepository.delete(scheme.getId());
    schemeRepository.flush();

    assertNull(schemeRepository.findOne(scheme.getId()));
  }

  @Test
  public void shouldSaveSchemePropertyValueInsertions() {
    Scheme scheme = exampleSchemeWithProperties();

    schemeRepository.saveAndFlush(scheme);
    assertSchemeEqualsIgnoreAuditData(scheme, schemeRepository.findOne(scheme.getId()));

    scheme.addProperty("note", "en", "A New Property Value");
    assertSchemeNotEqualsIgnoreAuditData(scheme, schemeRepository.findOne(scheme.getId()));

    schemeRepository.saveAndFlush(scheme);
    assertSchemeEqualsIgnoreAuditData(scheme, schemeRepository.findOne(scheme.getId()));
  }

  @Test
  public void shouldSaveSchemePropertyValueModifications() {
    Scheme scheme = exampleSchemeWithProperties();

    schemeRepository.saveAndFlush(scheme);
    assertSchemeEqualsIgnoreAuditData(scheme, schemeRepository.findOne(scheme.getId()));

    scheme.addProperty("prefLabel", "en", "Edited Existing Scheme Label");
    assertSchemeNotEqualsIgnoreAuditData(scheme, schemeRepository.findOne(scheme.getId()));

    schemeRepository.saveAndFlush(scheme);
    assertSchemeEqualsIgnoreAuditData(scheme, schemeRepository.findOne(scheme.getId()));
  }

  @Test
  public void shouldSaveSchemePropertyValueRemoval() {
    Scheme scheme = exampleSchemeWithProperties();

    schemeRepository.saveAndFlush(scheme);
    assertSchemeEqualsIgnoreAuditData(scheme, schemeRepository.findOne(scheme.getId()));

    scheme.getProperties().clear();
    assertSchemeNotEqualsIgnoreAuditData(scheme, schemeRepository.findOne(scheme.getId()));

    schemeRepository.saveAndFlush(scheme);
    assertSchemeEqualsIgnoreAuditData(scheme, schemeRepository.findOne(scheme.getId()));
  }

}
