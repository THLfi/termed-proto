package fi.thl.termed.util;

import com.google.common.collect.Lists;

import org.junit.Test;

import java.io.StringWriter;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CsvTableWriterTest {

  @Test
  public void shouldWriteRowsAsCsv() {
    List<String[]> rows = Lists.newArrayList(new String[]{"id", "name"},
                                             new String[]{"1", "George"},
                                             new String[]{"2", "Jack"});

    StringWriter stringWriter = new StringWriter();
    new CsvTableWriter(stringWriter).write(rows);

    String expected = "\"id\",\"name\"\n\"1\",\"George\"\n\"2\",\"Jack\"\n";

    assertEquals(expected, stringWriter.toString());

  }

}