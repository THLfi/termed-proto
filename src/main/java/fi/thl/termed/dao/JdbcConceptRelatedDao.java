package fi.thl.termed.dao;

import com.google.common.collect.Sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import fi.thl.termed.model.Resource;

public class JdbcConceptRelatedDao {

  @SuppressWarnings("all")
  private Logger log = LoggerFactory.getLogger(getClass());

  private final JdbcTemplate jdbcTemplate;
  private final Properties sqlQueries;
  private final RowMapper<Resource> resourceRowMapper;


  public JdbcConceptRelatedDao(DataSource dataSource, Properties sqlQueries) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
    this.sqlQueries = sqlQueries;
    this.resourceRowMapper = new ResourceRowMapper();
  }

  public void saveRelated(String conceptId, List<Resource> related) {
    saveRelated(conceptId, getRelated(conceptId), related);
  }

  public void saveRelated(String conceptId,
                          List<Resource> oldRelatedResources,
                          List<Resource> newRelatedResources) {

    Set<Resource> oldRelated = Sets.newHashSet(oldRelatedResources);
    Set<Resource> newRelated = Sets.newHashSet(newRelatedResources);

    for (Resource removed : Sets.difference(oldRelated, newRelated)) {
      removeRelated(conceptId, removed.getId());
    }

    for (Resource added : Sets.difference(newRelated, oldRelated)) {
      insertRelated(conceptId, added.getId());
    }
  }

  private void insertRelated(String conceptId, String relatedId) {
    jdbcTemplate.update(sqlQueries.getProperty("related-insert-concept_id-related_id"), conceptId,
                        relatedId);
    jdbcTemplate.update(sqlQueries.getProperty("related-insert-concept_id-related_id"), relatedId,
                        conceptId);
  }

  private void removeRelated(String conceptId, String relatedId) {
    jdbcTemplate
        .update(sqlQueries.getProperty("related-delete-by-concept_id-and-related_id"), conceptId,
                relatedId);
    jdbcTemplate
        .update(sqlQueries.getProperty("related-delete-by-concept_id-and-related_id"), relatedId,
                conceptId);
  }


  public List<Resource> getRelated(String conceptId) {
    return jdbcTemplate.query(sqlQueries.getProperty("concept-find-related"),
                              resourceRowMapper, conceptId);
  }

  public void removeRelated(String conceptId) {
    jdbcTemplate.update(sqlQueries.getProperty("related-delete-by-concept_id"), conceptId);
    jdbcTemplate.update(sqlQueries.getProperty("related-delete-by-related_id"), conceptId);
  }

}
