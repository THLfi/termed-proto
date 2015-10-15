package fi.thl.termed.controller;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import fi.thl.termed.model.Concept;
import fi.thl.termed.service.CrudService;
import fi.thl.termed.util.ExcelTableWriter;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping(value = "/")
public class ExcelExporter {

  public static final String XLSX_CONTENT_TYPE =
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
  private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
  @SuppressWarnings("all")
  private Logger log = LoggerFactory.getLogger(getClass());
  @Autowired
  private CrudService crudService;

  @RequestMapping(method = GET, value = "export/{schemeId}/excel-legacy")
  @Transactional
  public void exportExcel(@PathVariable("schemeId") String schemeId, HttpServletResponse response)
      throws IOException {
    log.info("Exporting Excel {}", schemeId);

    final String lang = "fi";

    List<Concept> rootConcepts =
        crudService.query(Concept.class,
                          "+scheme.id:" + schemeId + " -partOf.id:[* TO *] +type.id:[* TO *]",
                          0, -1, Lists.newArrayList("prefLabel.fi.sortable"));

    List<String[]> table = toTable(rootConcepts, lang);

    response.setContentType(XLSX_CONTENT_TYPE);
    response.setHeader("Content-Disposition", "attachment; filename=termed-" + today() + ".xlsx");

    new ExcelTableWriter(response.getOutputStream()).write(table);

    log.info("Done.");
  }

  private String today() {
    return df.format(Calendar.getInstance().getTime());
  }


  private List<String[]> toTable(List<Concept> rootConcepts, String lang) {
    List<String[]> table = Lists.newArrayList();

    List<String> headerRow = Lists.newArrayList();

    headerRow.add("id");
    headerRow.add("index");
    headerRow.add("prefLabel");
    headerRow.add("type");
    headerRow.add("parentId");
    headerRow.add("depth");
    headerRow.add("definition");
    headerRow.add("note");
    headerRow.add("example");
    headerRow.add("classification");
    headerRow.add("source");
    headerRow.add("changeNote");
    headerRow.add("comment");
    headerRow.add("repeatable");
    headerRow.add("required");

    table.add(headerRow.toArray(new String[headerRow.size()]));

    for (Concept rootConcept : rootConcepts) {
      toTable(rootConcept, null, 0, table, lang);
      // add empty row
      table.add(new String[]{});
    }

    return table;
  }

  private void toTable(Concept concept, Concept parentConcept, int depth, List<String[]> table,
                       String lang) {

    List<String> row = Lists.newArrayList();

    row.add(concept.getId());
    row.add(concept.getPropertyValue("index", lang));
    row.add(concept.getPropertyValue("prefLabel", lang));
    row.add(getTypePrefLabel(concept, lang));
    row.add(parentConcept != null ? parentConcept.getId() : "");
    row.add(String.valueOf(depth));
    row.add(concept.getPropertyValue("definition", lang));
    row.add(concept.getPropertyValue("note", lang));
    row.add(concept.getPropertyValue("example", lang));
    row.add(concept.getPropertyValue("classification", lang));
    row.add(concept.getPropertyValue("source", lang));
    row.add(concept.getPropertyValue("changeNote", lang));
    row.add(concept.getPropertyValue("comment", lang));
    row.add(concept.getPropertyValue("repeatable", lang));
    row.add(concept.getPropertyValue("required", lang));

    table.add(row.toArray(new String[row.size()]));

    List<Concept> parts = crudService.query(Concept.class,
                                            new TermQuery(new Term("partOf.id", concept.getId())),
                                            0, -1, Lists.newArrayList("index.sortable"));

    for (Concept part : parts) {
      toTable(part, concept, depth + 1, table, lang);
    }
  }

  private String getTypePrefLabel(Concept concept, String lang) {
    List<Concept> types = concept.getReferencesByType("type");
    List<String> typesPrefLabels = Lists.newArrayList();

    for (Concept type : types) {
      typesPrefLabels.add(type.getPropertyValue("prefLabel", lang));
    }

    return Joiner.on(", ").join(typesPrefLabels);
  }

}