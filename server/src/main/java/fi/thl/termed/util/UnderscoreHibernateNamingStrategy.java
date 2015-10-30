package fi.thl.termed.util;

import org.hibernate.cfg.ImprovedNamingStrategy;

/**
 * Naming strategy for tables and keys used by Hibernate. Uses underscores over cameCase e.g. tables
 * are named like foo_bar_baz rather than fooBarBaz.
 */
public class UnderscoreHibernateNamingStrategy extends ImprovedNamingStrategy {

  /**
   * Extend default functionality of ImprovedNamingStrategy by adding "_id" suffix to foreign keys
   */
  @Override
  public String foreignKeyColumnName(String propertyName,
                                     String propertyEntityName,
                                     String propertyTableName,
                                     String referencedColumnName) {

    String foreignKeyColumnName = super.foreignKeyColumnName(propertyName,
                                                             propertyEntityName,
                                                             propertyTableName,
                                                             referencedColumnName);

    return foreignKeyColumnName.endsWith("_id") ? foreignKeyColumnName
                                                : foreignKeyColumnName + "_id";
  }

}
