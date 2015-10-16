package fi.thl.termed.util;

import au.com.bytecode.opencsv.CSVWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class CsvTableWriter implements TableWriter {

  private Logger log = LoggerFactory.getLogger(getClass());

  private CSVWriter writer;

  public CsvTableWriter(Writer writer) {
    this(new CSVWriter(writer));
  }

  public CsvTableWriter(CSVWriter writer) {
    this.writer = writer;
  }

  @Override
  public void write(List<String[]> rows) {
    writer.writeAll(rows);
    close();
  }

  private void close() {
    try {
      writer.close();
    } catch (IOException e) {
      log.error("", e);
    }
  }

}
