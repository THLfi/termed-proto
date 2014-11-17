package fi.thl.termed.dao;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import fi.thl.termed.model.Resource;

public class ResourceRowMapper implements RowMapper<Resource> {

  @Override
  public Resource mapRow(ResultSet resultSet, int i) throws SQLException {
    return new Resource(resultSet.getString("id"));
  }

}
