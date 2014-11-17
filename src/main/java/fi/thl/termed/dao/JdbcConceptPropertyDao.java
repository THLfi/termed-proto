package fi.thl.termed.dao;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import fi.thl.termed.model.PropertyValue;

public class JdbcConceptPropertyDao {

  @SuppressWarnings("all")
  private Logger log = LoggerFactory.getLogger(getClass());

  private final JdbcTemplate jdbcTemplate;
  private final Properties sqlQueries;

  public JdbcConceptPropertyDao(DataSource dataSource, Properties sqlQueries) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
    this.sqlQueries = sqlQueries;
  }

  public void saveProperties(String conceptId,
                             Map<String, PropertyValue> properties) {
    saveProperties(conceptId, getProperties(conceptId), properties);
  }

  public void saveProperties(String conceptId,
                             Map<String, PropertyValue> oldProperties,
                             Map<String, PropertyValue> newProperties) {

    Set<Map.Entry<String, PropertyValue>> oldRelated = oldProperties.entrySet();
    Set<Map.Entry<String, PropertyValue>> newRelated = newProperties.entrySet();

    for (Map.Entry<String, PropertyValue> removed : Sets.difference(oldRelated, newRelated)) {
      PropertyValue value = removed.getValue();
      removePropertyValue(conceptId, removed.getKey(), value.getLang(), value.getLang());
    }

    for (Map.Entry<String, PropertyValue> added : Sets.difference(newRelated, oldRelated)) {
      PropertyValue value = added.getValue();
      insertPropertyValue(conceptId, added.getKey(), value.getLang(), value.getLang());
    }
  }

  private void insertPropertyValue(String conceptId, String propertyId, String lang, String value) {
    jdbcTemplate.update(sqlQueries.getProperty("property-insert-concept_id-property_id-lang-value"),
                        conceptId, propertyId, lang, value);
  }

  private void removePropertyValue(String conceptId, String propertyId, String lang, String value) {
    jdbcTemplate.update(
        sqlQueries.getProperty("property-delete-by-concept_id-and-property_id-and-lang-and-value"),
        conceptId, propertyId, lang, value);
  }

  public Map<String, PropertyValue> getProperties(String conceptId) {
    final Map<String, PropertyValue> properties = Maps.newHashMap();

    jdbcTemplate
        .query(sqlQueries.getProperty("property-find-by-concept_id"), new RowCallbackHandler() {
          @Override
          public void processRow(ResultSet resultSet) throws SQLException {
            properties.put(resultSet.getString("property_id"),
                           new PropertyValue(resultSet.getString("lang"),
                                             resultSet.getString("value")));
          }
        }, conceptId);

    return properties;
  }

  public void removeProperties(String conceptId) {
    jdbcTemplate.update(sqlQueries.getProperty("property-delete-by-concept_id"), conceptId);
  }

}
