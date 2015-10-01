package fi.thl.termed.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ExcelTableWriter implements TableWriter {

  private Logger log = LoggerFactory.getLogger(getClass());

  private OutputStream out;
  private Workbook wb;
  private Sheet sheet;

  private CellStyle cellStyle;

  public ExcelTableWriter(OutputStream out) {
    this.out = out;
    this.wb = new SXSSFWorkbook();
    this.sheet = wb.createSheet();
    this.cellStyle = wb.createCellStyle();

    cellStyle.setWrapText(true);
    cellStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
  }

  @Override
  public void write(List<String[]> data) {
    int rowNumber = 0;
    for (String[] rowData : data) {
      writeRow(sheet.createRow(rowNumber), rowData);
      sheet.autoSizeColumn(rowNumber);
      rowNumber++;
    }
    writeAndClose();
  }

  private void writeRow(Row row, String[] rowData) {
    for (int i = 0; i < rowData.length; i++) {
      Cell cell = row.createCell(i);
      cell.setCellType(Cell.CELL_TYPE_STRING);
      cell.setCellValue(rowData[i]);
      cell.setCellStyle(cellStyle);
    }
    row.setHeight((short) 700);
  }

  private void writeAndClose() {
    try {
      wb.write(out);
      out.close();
    } catch (IOException e) {
      log.error("", e);
    }
  }

}
