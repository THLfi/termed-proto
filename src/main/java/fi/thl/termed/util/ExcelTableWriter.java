package fi.thl.termed.util;

import org.apache.poi.ss.usermodel.Cell;
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

    public ExcelTableWriter(OutputStream out) {
        this.out = out;
        this.wb = new SXSSFWorkbook();
        this.sheet = wb.createSheet();
    }

    @Override
    public void write(List<String[]> data) {
        int rows = 0;
        for (String[] rowData : data) {
            writeRow(sheet.createRow(rows++), rowData);
        }
        writeAndClose();
    }

    private void writeRow(Row row, String[] rowData) {
        for (int i = 0; i < rowData.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue(rowData[i]);
        }
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
