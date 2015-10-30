package fi.thl.termed.util;

import com.google.common.collect.Lists;

import org.junit.Test;

import java.io.StringReader;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class CsvTableReaderTest {

  @Test
  public void shouldReadAllLinesFromCsv() {
    String csv = "\"id\",\"name\"\n\"1\",\"George\"\n\"2\",\"Jack\"\n";

    CsvTableReader reader = new CsvTableReader(new StringReader(csv));

    List<String[]> expected = Lists.newArrayList(new String[]{"id", "name"},
                                                 new String[]{"1", "George"},
                                                 new String[]{"2", "Jack"});
    List<String[]> actual = reader.read();

    assertEquals(expected.size(), actual.size());
    for (int i = 0; i < expected.size(); i++) {
      assertArrayEquals(expected.get(i), actual.get(i));
    }
  }

}