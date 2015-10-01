package fi.thl.termed.model;

import com.google.common.collect.Lists;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
public class CollectionIT {

  @PersistenceContext
  private EntityManager em;

  @Test
  public void shouldAddConceptToCollection() {
    Scheme exampleScheme = em.merge(new Scheme("exampleScheme"));

    Concept concept = new Concept("exampleConcept");
    concept.setScheme(exampleScheme);
    concept = em.merge(concept);

    Collection collection = new Collection("exampleCollection");
    collection.setScheme(exampleScheme);
    collection.setMembers(Lists.newArrayList(concept));
    em.merge(collection);
    em.flush();

    assertEquals(1, em.find(Collection.class, "exampleCollection").getMembers().size());
  }

}
