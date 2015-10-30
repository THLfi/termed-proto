package fi.thl.termed.serializer;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class DateConverterTest {

  @Test
  public void shouldConvertDateToStringAndBack() {
    DateConverter dateConverter = new DateConverter();
    Date date = new Date();
    assertEquals(date, dateConverter.reverse().convert(dateConverter.convert(date)));
  }

}