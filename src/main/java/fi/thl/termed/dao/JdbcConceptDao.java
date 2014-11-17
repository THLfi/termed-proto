package fi.thl.termed.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
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

  private final RowMapper<Concept> conceptRowMapper = new RowMapper<Concept>() {
    @Override
    public Concept mapRow(ResultSet resultSet, int i) throws SQLException {
      Concept concept = new Concept();
      concept.setId(resultSet.getString("id"));
      concept.setBroader(get(resultSet.getString("broader_id")));
      concept.setNarrower(findSimpleNarrower(resultSet.getString("id")));
      concept.setRelated(relatedDao.getRelated(resultSet.getString("id")));
      concept.setProperties(propertyDao.getProperties(resultSet.getString("id")));
      return concept;
    }
  };

  private final RowMapper<Concept> simpleConceptRowMapper = new RowMapper<Concept>() {
    @Override
    public Concept mapRow(ResultSet resultSet, int i) throws SQLException {
      Concept concept = new Concept();
      concept.setId(resultSet.getString("id"));
      concept.setProperties(propertyDao.getProperties(resultSet.getString("id")));
      return concept;
    }
  };


  @Autowired
  public JdbcConceptDao(DataSource dataSource, Properties sqlQueries) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
    this.sqlQueries = sqlQueries;
    this.relatedDao = new JdbcConceptRelatedDao(dataSource, sqlQueries, simpleConceptRowMapper);
    this.propertyDao = new JdbcConceptPropertyDao(dataSource, sqlQueries);
  }

  @Override
  public Concept save(Concept concept) {
    if (concept.hasId() && exists(concept.getId())) {
      update(get(concept.getId()), concept);
    } else {
      insert(concept);
    }
    return concept;
  }

  private void insert(Concept concept) {
    concept.ensureId();
    jdbcTemplate.update(
        sqlQueries.getProperty("concept-insert-id-broader_id"),
        concept.getId(), concept.getBroaderId());
    propertyDao.saveProperties(concept.getId(), concept.getProperties());
    relatedDao.saveRelated(concept.getId(), concept.getRelated());
  }

  private void update(Concept old, Concept concept) {
    jdbcTemplate.update(
        sqlQueries.getProperty("concept-update-broader_id-by-id"), concept.getBroaderId(),
        concept.getId());
    propertyDao.saveProperties(concept.getId(), old.getProperties(), concept.getProperties());
    relatedDao.saveRelated(concept.getId(), old.getRelated(), concept.getRelated());
  }

  private Concept findSimpleOne(String id) {
    return exists(id) ? jdbcTemplate.queryForObject(sqlQueries.getProperty("concept-find-by-id"),
                                                    simpleConceptRowMapper, id) : null;
  }

  private List<Concept> findSimpleNarrower(String broaderId) {
    return jdbcTemplate.query(sqlQueries.getProperty("concept-find-by-broader_id"),
                              simpleConceptRowMapper, broaderId);
  }

  @Override
  public Concept get(String id) {
    return exists(id) ? jdbcTemplate.queryForObject(sqlQueries.getProperty("concept-find-by-id"),
                                                    conceptRowMapper, id) : null;
  }

  @Override
  public List<Concept> query() {
    return jdbcTemplate.query(sqlQueries.getProperty("concept-find-all"),
                              simpleConceptRowMapper);
  }

  @Override
  public List<Concept> query(int max) {
    return jdbcTemplate.query(sqlQueries.getProperty("concept-find-top-n"),
                              simpleConceptRowMapper, max);
  }

  @Override
  public List<Concept> query(String query, int max) {
    return jdbcTemplate.query(sqlQueries.getProperty("concept-find-top-n-by-property-value"),
                              simpleConceptRowMapper, query, max);
  }

  @Override
  public void remove(String id) {
    remove(get(id));
  }

  private void remove(Concept concept) {
    if (concept == null || !concept.hasId() || !exists(concept.getId())) {
      log.warn("Failed to delete concept: {}", concept);
      return;
    }

    for (Resource narrower : concept.getNarrower()) {
      remove(narrower.getId());
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
