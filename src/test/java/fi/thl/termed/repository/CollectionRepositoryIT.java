package fi.thl.termed.repository;

import com.google.common.collect.Lists;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fi.thl.termed.model.Collection;
import fi.thl.termed.model.Concept;
import fi.thl.termed.model.Scheme;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/applicationContext.xml",
                                   "file:src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml",
                                   "file:src/main/webapp/WEB-INF/spring-security.xml"})
@TransactionConfiguration
@Transactional
public class CollectionRepositoryIT {

  @Autowired
  private SchemeRepository schemeRepository;

  @Autowired
  private ConceptRepository conceptRepository;

  @Autowired
  private CollectionRepository collectionRepository;

  @Test
  public void shouldAddConceptToCollection() {
    Scheme exampleScheme = new Scheme("exampleScheme");
    schemeRepository.saveAndFlush(exampleScheme);

    Concept concept = new Concept("exampleConcept");
    concept.setScheme(exampleScheme);
    conceptRepository.saveAndFlush(concept);

    Collection collection = new Collection("exampleCollection");
    collection.setScheme(exampleScheme);
    collection.setMembers(Lists.newArrayList(concept));
    collectionRepository.saveAndFlush(collection);

    assertEquals(1, collectionRepository.findOne("exampleCollection").getMembers().size());
  }

}
