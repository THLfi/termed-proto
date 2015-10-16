package fi.thl.termed.util;

import au.com.bytecode.opencsv.CSVReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.List;

public class CsvTableReader implements TableReader {

  private Logger log = LoggerFactory.getLogger(getClass());

  private CSVReader reader;

  public CsvTableReader(Reader reader) {
    this(new CSVReader(reader));
  }

  public CsvTableReader(CSVReader reader) {
    this.reader = reader;
  }

  @Override
  public List<String[]> read() {
    try {
      return reader.readAll();
    } catch (IOException e) {
      log.error("", e);
      return Collections.emptyList();
    } finally {
      try {
        reader.close();
      } catch (IOException e) {
        log.error("", e);
      }
    }
  }

}
