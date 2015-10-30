package fi.thl.termed.serializer;

import com.google.common.base.Converter;

import org.joda.time.DateTime;

import java.util.Date;

/**
 * Convert Date to ISO 8601 datetime string.
 */
public class DateConverter extends Converter<Date, String> {

  @Override
  protected String doForward(Date date) {
    return date != null ? new DateTime(date).toString() : null;
  }

  @Override
  protected Date doBackward(String date) {
    return date != null ? new DateTime(date).toDate() : null;
  }

}
