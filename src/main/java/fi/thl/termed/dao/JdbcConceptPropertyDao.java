package fi.thl.termed.dao;

import com.google.common.base.Predicates;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import fi.thl.termed.util.MapUtils;
import fi.thl.termed.util.Pair;

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
                             Map<String, Map<String, String>> properties) {
    saveProperties(conceptId, getProperties(conceptId), properties);
  }

  public void saveProperties(String conceptId,
                             Map<String, Map<String, String>> oldProperties,
                             Map<String, Map<String, String>> newProperties) {

    MapDifference<Pair<String, String>, String> diff =
        Maps.difference(
            Maps.filterValues(MapUtils.collapseKeys(oldProperties), Predicates.notNull()),
            Maps.filterValues(MapUtils.collapseKeys(newProperties), Predicates.notNull()));

    for (Map.Entry<Pair<String, String>, String> added : diff.entriesOnlyOnRight().entrySet()) {
      Pair<String, String> propLang = added.getKey();
      insertPropertyValue(conceptId, propLang.getLeft(), propLang.getRight(), added.getValue());
    }
    for (Map.Entry<Pair<String, String>, MapDifference.ValueDifference<String>> modified : diff
        .entriesDiffering().entrySet()) {
      Pair<String, String> propLang = modified.getKey();
      updatePropertyValue(conceptId, propLang.getLeft(), propLang.getRight(),
                          modified.getValue().rightValue());
    }
    for (Map.Entry<Pair<String, String>, String> removed : diff.entriesOnlyOnLeft().entrySet()) {
      Pair<String, String> propLang = removed.getKey();
      removePropertyValue(conceptId, propLang.getLeft(), propLang.getRight(), removed.getValue());
    }
  }

  private void insertPropertyValue(String conceptId, String propertyId, String lang, String value) {
    jdbcTemplate.update(sqlQueries.getProperty("property-insert-concept_id-property_id-lang-value"),
                        conceptId, propertyId, lang, value);
  }

  private void updatePropertyValue(String conceptId, String propertyId, String lang, String value) {
    jdbcTemplate.update(
        sqlQueries.getProperty("property-update-value-by-concept_id-and-property_id-and-lang"),
        value, conceptId, propertyId, lang);
  }

  private void removePropertyValue(String conceptId, String propertyId, String lang, String value) {
    jdbcTemplate.update(
        sqlQueries.getProperty("property-delete-by-concept_id-and-property_id-and-lang-and-value"),
        conceptId, propertyId, lang, value);
  }

  public Map<String, Map<String, String>> getProperties(String conceptId) {
    return MapUtils.expandKeys(getPropertyMap(conceptId));
  }

  private Map<Pair<String, String>, String> getPropertyMap(String conceptId) {
    final Map<Pair<String, String>, String> properties = Maps.newHashMap();

    jdbcTemplate
        .query(sqlQueries.getProperty("property-find-by-concept_id"), new RowCallbackHandler() {
          @Override
          public void processRow(ResultSet resultSet) throws SQLException {
            properties.put(new Pair<String, String>(resultSet.getString("property_id"),
                                                    resultSet.getString("lang")),
                           resultSet.getString("value"));
          }
        }, conceptId);

    return properties;
  }

  public void removeProperties(String conceptId) {
    jdbcTemplate.update(sqlQueries.getProperty("property-delete-by-concept_id"), conceptId);
  }

}
