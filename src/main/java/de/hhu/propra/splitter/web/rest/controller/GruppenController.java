package de.hhu.propra.splitter.web.rest.controller;

import de.hhu.propra.splitter.services.GruppenService;
import de.hhu.propra.splitter.web.rest.objects.Gruppe;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class GruppenController {

  private final GruppenService gruppenService;

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public GruppenController(final GruppenService gruppenService) {
    this.gruppenService = gruppenService;
  }

  @PostMapping("/gruppen")
  public ResponseEntity<String> gruppeErstellen(
      @RequestBody Gruppe gruppe
  ) {
    Long gruppenId = gruppenService.addGruppe(gruppe.personen().get(0), gruppe.name());
    for (String person : gruppe.personen().stream().skip(1).toList()) {
      gruppenService.addPersonToGruppe(person, gruppenId);
    }

    return new ResponseEntity<>(gruppenId.toString(), HttpStatus.CREATED);
  }


}
