package fi.thl.termed.service;

import com.hp.hpl.jena.rdf.model.Model;

public interface ConceptRdfService {

  Model getScheme(String schemeId);

  void saveScheme(Model model);

  void saveScheme(String schemeId, Model model);

}
