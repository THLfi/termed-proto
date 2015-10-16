package fi.thl.termed.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fi.thl.termed.service.ConceptTableService;
import fi.thl.termed.util.CsvTableReader;
import fi.thl.termed.util.CsvTableWriter;
import fi.thl.termed.util.ExcelTableWriter;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping(value = "/")
public class TableController {

  public static final String CSV_CONTENT_TYPE = "text/csv;charset=utf-8";
  public static final String TEXT_CONTENT_TYPE = "text/plain;charset=utf-8";
  public static final String XLSX_CONTENT_TYPE =
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

  @Autowired
  private ConceptTableService tableService;

  private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");


  @RequestMapping(method = POST, value = "import/{schemeId}/csv")
  public void importCsv(@PathVariable("schemeId") String schemeId,
                        HttpServletRequest request) throws IOException {
    List<String[]> rows = new CsvTableReader(request.getReader()).read();
    tableService.saveTable(schemeId, rows);
  }

  @RequestMapping(method = GET, value = "export/{schemeId}/csv")
  public void exportCsv(@PathVariable("schemeId") String schemeId,
                        @RequestParam(value = "select", required = false, defaultValue = "") List<String> cols,
                        @RequestParam(value = "query", required = false, defaultValue = "") String query,
                        @RequestParam(value = "first", required = false, defaultValue = "0") Integer first,
                        @RequestParam(value = "max", required = false, defaultValue = "-1") Integer max,
                        @RequestParam(value = "orderBy", required = false, defaultValue = "") List<String> orderBy,
                        @RequestParam(value = "download", required = false, defaultValue = "false") boolean download,
                        HttpServletResponse response) throws IOException {

    List<String[]> table =
        tableService.queryTable(schemeId, parseTableHeaders(cols), query, first, max, orderBy);

    if (download) {
      response.setHeader("Content-Disposition", "attachment; filename=topi-" + today() + ".csv");
      response.setContentType(CSV_CONTENT_TYPE);
    } else {
      response.setContentType(TEXT_CONTENT_TYPE);
    }

    new CsvTableWriter(response.getWriter()).write(table);
  }

  @RequestMapping(method = GET, value = "export/{schemeId}/excel")
  public void exportExcel(@PathVariable("schemeId") String schemeId,
                          @RequestParam(value = "select", required = false, defaultValue = "") List<String> cols,
                          @RequestParam(value = "query", required = false, defaultValue = "") String query,
                          @RequestParam(value = "first", required = false, defaultValue = "0") Integer first,
                          @RequestParam(value = "max", required = false, defaultValue = "-1") Integer max,
                          @RequestParam(value = "orderBy", required = false, defaultValue = "") List<String> orderBy,
                          HttpServletResponse response) throws IOException {

    List<String[]> table =
        tableService.queryTable(schemeId, parseTableHeaders(cols), query, first, max, orderBy);

    response.setContentType(XLSX_CONTENT_TYPE);
    response.setHeader("Content-Disposition", "attachment; filename=termed-" + today() + ".xlsx");

    new ExcelTableWriter(response.getOutputStream()).write(table);
  }

  private String today() {
    return df.format(Calendar.getInstance().getTime());
  }

  private Map<String, String> parseTableHeaders(List<String> cols) {
    Map<String, String> keyNameMap = Maps.newLinkedHashMap();

    for (String col : splitEach(cols, ",")) {
      String[] keyAndName = col.split(":", 2);
      keyNameMap.put(keyAndName[0], keyAndName.length == 2 ? keyAndName[1] : keyAndName[0]);
    }

    return keyNameMap;
  }

  private List<String> splitEach(List<String> strings, String regex) {
    List<String> result = Lists.newArrayList();
    for (String string : strings) {
      result.addAll(Lists.newArrayList(string.split(regex)));
    }
    return result;
  }

}
