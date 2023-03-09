package de.hhu.propra.splitter.web.rest.controller;

import de.hhu.propra.splitter.domain.models.Gruppe;
import de.hhu.propra.splitter.domain.models.Person;
import de.hhu.propra.splitter.domain.services.AusgleichService;
import de.hhu.propra.splitter.exceptions.PersonNotFoundException;
import de.hhu.propra.splitter.services.GruppenService;
import de.hhu.propra.splitter.web.rest.objects.AusgabeEntity;
import de.hhu.propra.splitter.web.rest.objects.DetailedGruppeEntity;
import de.hhu.propra.splitter.web.rest.objects.SimpleGruppenEntity;
import de.hhu.propra.splitter.web.rest.objects.UeberweisungEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.Valid;
import org.javamoney.moneta.Money;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    this.gruppenService.addGruppe("ThiloSavaryHHU", "Gruppe 1");
    this.gruppenService.addPersonToGruppe("Mick", 0L);
    this.gruppenService.addPersonToGruppe("Keith", 0L);
    this.gruppenService.addPersonToGruppe("Ronnie", 0L);
  }

  @PostMapping("/gruppen")
  public ResponseEntity<String> gruppeErstellen(
      @RequestBody @Valid SimpleGruppenEntity gruppe,
      BindingResult bindingResult
  ) {
    if (bindingResult.hasErrors()) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    Long gruppenId = gruppenService.addGruppe(gruppe.personen().get(0), gruppe.name());
    for (String person : gruppe.personen().stream().skip(1).toList()) {
      gruppenService.addPersonToGruppe(person, gruppenId);
    }

    return new ResponseEntity<>(gruppenId.toString(), HttpStatus.CREATED);
  }


  @GetMapping("/user/{GITHUB-LOGIN}/gruppen")
  public List<SimpleGruppenEntity> allGruppenForUser(
      @PathVariable("GITHUB-LOGIN") String login
  ) {
    Set<de.hhu.propra.splitter.domain.models.Gruppe> gruppen =
        gruppenService.getGruppenForGithubName(login);

    return gruppen.stream().map(a -> new SimpleGruppenEntity(a.getId().toString(), a.getName(),
        a.getMitglieder().stream().map(Person::getGitHubName).toList())).toList();
  }

  @GetMapping("/gruppen/{id}")
  public DetailedGruppeEntity gruppenDetailsById(@PathVariable("id") String gruppenId) {
    Gruppe gruppe = gruppenService.getGruppeById(gruppenId);

    return new DetailedGruppeEntity(gruppenId, gruppe.getName(),
        gruppe.getMitglieder().stream().map(Person::getGitHubName).toList(), !gruppe.isOffen(),
        gruppe.getAusgaben().stream().map(
            a -> new AusgabeEntity(
                a.getBeschreibung(),
                a.getGlaeubiger().getGitHubName(),
                a.getBetrag().multiply(100L).getNumber().intValue(), //TODO:...
                a.getSchuldner().stream().map(Person::getGitHubName).toList()
            )).toList());
  }

  @PostMapping("/gruppen/{id}/schliessen")
  public ResponseEntity<String> gruppeSchliessen(
      @PathVariable("id") String gruppenId
  ) {
    Gruppe gruppe = gruppenService.getGruppeById(gruppenId);
    gruppenService.schliesseGruppe(gruppe.getId());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping("/gruppen/{id}/auslagen")
  public ResponseEntity<String> addAusgabeToGruppe(
      @PathVariable("id") String gruppenId,
      @RequestBody @Valid AusgabeEntity ausgabe,
      BindingResult bindingResult
  ) {
    if (bindingResult.hasErrors()) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    Gruppe gruppe = gruppenService.getGruppeById(gruppenId);
    if (!gruppe.isOffen()) {
      return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
    try {
      gruppenService.addAusgabe(
          gruppe.getId(),
          ausgabe.grund(),
          Money.of(ausgabe.cent(), "EUR").divide(100),
          ausgabe.glaeubiger(),
          new HashSet<>(ausgabe.schuldner())
      );
    } catch (PersonNotFoundException e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @GetMapping("/gruppen/{id}/ausgleich")
  public List<UeberweisungEntity> getUeberweisungenForGruppe(
      @PathVariable("id") String gruppenId
  ) {
    Gruppe gruppe = gruppenService.getGruppeById(gruppenId);
    AusgleichService ausgleichService = new AusgleichService();
    return ausgleichService.berechneAusgleichUeberweisungen(gruppe).stream().map(
        ueberweisung -> new UeberweisungEntity(
            ueberweisung.getSender().getGitHubName(),
            ueberweisung.getEmpfaenger().getGitHubName(),
            ueberweisung.getBetrag().multiply(100L).getNumber().intValue()
        )
    ).toList();
  }
}
