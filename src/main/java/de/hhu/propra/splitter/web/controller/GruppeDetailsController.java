package de.hhu.propra.splitter.web.controller;

import de.hhu.propra.splitter.domain.models.Gruppe;
import de.hhu.propra.splitter.domain.models.Person;
import de.hhu.propra.splitter.domain.models.Ueberweisung;
import de.hhu.propra.splitter.domain.services.AusgleichService;
import de.hhu.propra.splitter.services.GruppenService;
import de.hhu.propra.splitter.web.objects.AusgabeWebobject;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GruppeDetailsController {

  final GruppenService gruppenService;


  @SuppressFBWarnings("EI_EXPOSE_REP2")
  @Autowired
  public GruppeDetailsController(final GruppenService gruppenService) {
    this.gruppenService = gruppenService;
  }

  @GetMapping("/gruppe")
  public String gruppeDetailsView(
      Model m,
      OAuth2AuthenticationToken token,
      @RequestParam(value = "nr") long gruppeId
  ) {
    String username = token
        .getPrincipal()
        .getAttribute("login");
    Gruppe gruppe = gruppenService.getGruppeForGithubNameById(
        username,
        gruppeId
    );
    List<AusgabeWebobject> ausgaben = gruppe
        .getAusgaben()
        .stream()
        .map(
            e -> {
              return new AusgabeWebobject(
                  e.getBeschreibung(),
                  e
                      .getBetrag()
                      .toString(),
                  e
                      .getGlaeubiger()
                      .getGitHubName(),
                  String.join(
                      ", ",
                      e
                          .getSchuldner()
                          .stream()
                          .map(Person::getGitHubName)
                          .toList()
                  ),
                  (
                      e
                          .getGlaeubiger()
                          .getGitHubName()
                          .equals(username)
                          || e
                          .getSchuldner()
                          .stream()
                          .map(Person::getGitHubName)
                          .toList()
                          .contains(username)
                  )
              );
            })
        .sorted(Comparator.comparing(AusgabeWebobject::glaeubiger)).toList();
    List<Person> persons = gruppe.getMitglieder().stream().sorted().toList();
    m.addAttribute(
        "gruppe",
        gruppe
    );
    m.addAttribute(
        "mitglieder",
        persons
    );
    m.addAttribute(
        "ausgaben",
        ausgaben
    );
    AusgleichService ausgleichService = new AusgleichService();
    List<Ueberweisung> ueberweisungen = ausgleichService.berechneAusgleichUeberweisungen(gruppe)
        .stream().sorted(
            Comparator.comparing(a -> a.getSender().getGitHubName())).toList();
    m.addAttribute(
        "ueberweisungen",
        ueberweisungen
    );
    return "gruppeDetails";
  }

}
