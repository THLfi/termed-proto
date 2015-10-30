package fi.thl.termed.web;

import com.google.common.base.Charsets;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import fi.thl.termed.service.ConceptRdfService;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = "/api/rdf")
public class RdfController {

  private final ConceptRdfService rdfService;

  @Autowired
  public RdfController(ConceptRdfService rdfService) {
    this.rdfService = rdfService;
  }

  @RequestMapping(method = GET, value = "/export/{schemeId}", produces = "text/turtle;charset=UTF-8")
  public void exportRdf(@PathVariable("schemeId") String schemeId, HttpServletResponse response)
      throws IOException {
    response.setCharacterEncoding(Charsets.UTF_8.toString());
    rdfService.getScheme(schemeId).write(response.getWriter(), "TTL");
  }

  @RequestMapping(method = POST, value = "/import/{schemeId}", consumes = "text/turtle;charset=UTF-8")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void importRdf(@PathVariable("schemeId") String schemeId, @RequestBody String input) {
    Model model = ModelFactory.createDefaultModel();
    model.read(new ByteArrayInputStream(input.getBytes(Charsets.UTF_8)), null, "TTL");
    rdfService.saveScheme(schemeId, model);
  }

}
