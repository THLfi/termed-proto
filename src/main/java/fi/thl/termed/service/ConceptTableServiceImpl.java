package fi.thl.termed.service;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import fi.thl.termed.util.JsonPredicates;
import fi.thl.termed.util.JsonUtils;
import fi.thl.termed.util.LuceneQueryStringUtils;
import fi.thl.termed.util.TableUtils;

@Service
@Transactional
public class ConceptTableServiceImpl implements ConceptTableService {

  @Autowired
  private JsonCrudService crudService;

  @Override
  public List<String[]> queryTable(String schemeId, Map<String, String> select, String query,
                                   int first, int max, List<String> orderBy) {
    JsonArray concepts = crudService.query("concepts", LuceneQueryStringUtils
        .and(LuceneQueryStringUtils.termQuery("scheme.id", schemeId), query), first, max, orderBy);

    List<Map<String, String>> rows = Lists.newArrayList();
    for (JsonElement concept : concepts) {
      rows.add(JsonUtils.flatten(concept));
    }

    return !select.isEmpty() ? TableUtils.toTable(select, rows) : TableUtils.toTable(rows);
  }

  @Override
  public void saveTable(String schemeId, List<String[]> rows) {
    JsonArray array = new JsonArray();

    for (Map<String, String> row : TableUtils.toMapped(rows)) {
      // make sure that we save to right scheme
      row.put("scheme.id", schemeId);
      array.add(clean(JsonUtils.unflatten(row)));
    }

    crudService.save("concepts", array);
  }

  @SuppressWarnings("unchecked")
  private JsonElement clean(JsonElement element) {
    return JsonUtils.filter(element,
                            JsonPredicates.notNull(),
                            JsonPredicates.stringDoesNotMatch("\\s*"),
                            JsonPredicates.notEmpty());
  }
}
