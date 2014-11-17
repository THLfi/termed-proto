package fi.thl.termed.dao;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import fi.thl.termed.model.Concept;

public class ConceptRowMapper implements RowMapper<Concept> {

  private final JdbcConceptDao conceptDao;
  private final JdbcConceptPropertyDao propertyDao;
  private final JdbcConceptRelatedDao relatedDao;

  public ConceptRowMapper(JdbcConceptDao conceptDao,
                          JdbcConceptPropertyDao propertyDao,
                          JdbcConceptRelatedDao relatedDao) {
    this.conceptDao = conceptDao;
    this.propertyDao = propertyDao;
    this.relatedDao = relatedDao;
  }

  @Override
  public Concept mapRow(ResultSet resultSet, int i) throws SQLException {
    Concept concept = new Concept();

    concept.setId(resultSet.getString("id"));
    concept.setType(conceptDao.findOne(resultSet.getString("type_id")));
    concept.setParent(conceptDao.findOne(resultSet.getString("parent_id")));
    concept.setProperties(propertyDao.getProperties(resultSet.getString("id")));
    concept.setChildren(conceptDao.getChildren(resultSet.getString("id")));
    concept.setRelated(relatedDao.getRelated(resultSet.getString("id")));

    return concept;
  }

}
