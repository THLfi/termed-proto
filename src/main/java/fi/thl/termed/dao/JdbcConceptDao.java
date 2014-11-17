package fi.thl.termed.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import fi.thl.termed.model.Concept;
import fi.thl.termed.model.Resource;

@Repository
public class JdbcConceptDao implements ConceptDao {

  @SuppressWarnings("all")
  private Logger log = LoggerFactory.getLogger(getClass());

  private final JdbcTemplate jdbcTemplate;
  private final Properties sqlQueries;
  private final JdbcConceptPropertyDao propertyDao;
  private final JdbcConceptRelatedDao relatedDao;
  private final RowMapper<Concept> conceptRowMapper;
  private final RowMapper<Resource> resourceRowMapper;

  @Autowired
  public JdbcConceptDao(DataSource dataSource, Properties sqlQueries) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
    this.sqlQueries = sqlQueries;
    this.relatedDao = new JdbcConceptRelatedDao(dataSource, sqlQueries);
    this.propertyDao = new JdbcConceptPropertyDao(dataSource, sqlQueries);
    this.conceptRowMapper = new ConceptRowMapper(this, propertyDao, relatedDao);
    this.resourceRowMapper = new ResourceRowMapper();
  }

  @Override
  public Concept save(Concept concept) {
    if (concept.hasId() && exists(concept.getId())) {
      update(findOne(concept.getId()), concept);
    } else {
      insert(concept);
    }
    return concept;
  }

  private void insert(Concept concept) {
    concept.ensureId();
    jdbcTemplate.update(
        sqlQueries.getProperty("concept-insert-id-type_id-parent_id"),
        concept.getId(), concept.getTypeId(), concept.getParentId());
    propertyDao.saveProperties(concept.getId(), concept.getProperties());
    relatedDao.saveRelated(concept.getId(), concept.getRelated());
  }

  private void update(Concept old, Concept concept) {
    jdbcTemplate.update(
        sqlQueries.getProperty("concept-update-type_id-parent_id-by-id"),
        concept.getTypeId(), concept.getParentId(), concept.getId());
    propertyDao.saveProperties(concept.getId(), old.getProperties(), concept.getProperties());
    relatedDao.saveRelated(concept.getId(), old.getRelated(), concept.getRelated());
  }

  public List<Resource> getChildren(String parentId) {
    return jdbcTemplate.query(sqlQueries.getProperty("concept-find-by-parent_id"),
                              resourceRowMapper, parentId);
  }

  @Override
  public Concept findOne(String id) {
    return exists(id) ? jdbcTemplate.queryForObject(sqlQueries.getProperty("concept-find-by-id"),
                                                    conceptRowMapper, id) : null;
  }

  @Override
  public List<Concept> findAll() {
    return jdbcTemplate.query(sqlQueries.getProperty("concept-find-all"), conceptRowMapper);
  }

  @Override
  public void remove(String id) {
    remove(findOne(id));
  }

  private void remove(Concept concept) {
    if (concept == null || !concept.hasId() || !exists(concept.getId())) {
      log.warn("Failed to delete concept: {}", concept);
      return;
    }

    for (Resource child : concept.getChildren()) {
      remove(child.getId());
    }

    relatedDao.removeRelated(concept.getId());
    propertyDao.removeProperties(concept.getId());
    jdbcTemplate.update(sqlQueries.getProperty("concept-delete-by-id"), concept.getId());
  }

  @Override
  public boolean exists(String id) {
    return jdbcTemplate.queryForObject(
        sqlQueries.getProperty("concept-count-by-id"), Long.class, id) == 1;
  }

}
