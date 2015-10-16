package fi.thl.termed.service;

import java.util.List;
import java.util.Map;

public interface ConceptTableService {

  List<String[]> queryTable(String schemeId, Map<String, String> select,
                            String query, int first, int max, List<String> orderBy);

  void saveTable(String schemeId, List<String[]> rows);

}
