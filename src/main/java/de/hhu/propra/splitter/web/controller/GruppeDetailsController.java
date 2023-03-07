package de.hhu.propra.splitter.web.controller;

import de.hhu.propra.splitter.domain.models.Gruppe;
import de.hhu.propra.splitter.domain.models.Person;
import de.hhu.propra.splitter.domain.models.Ueberweisung;
import de.hhu.propra.splitter.domain.services.AusgleichService;
import de.hhu.propra.splitter.services.GruppenService;
import de.hhu.propra.splitter.web.objects.AusgabeWebobject;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Set;
import java.util.stream.Collectors;
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

  //TODO: Change MoneyFormat
  @GetMapping("/gruppe")
  public String gruppeDetailsView(Model m, OAuth2AuthenticationToken token,
      @RequestParam(value = "nr") long gruppeId) {
    String username = token.getPrincipal().getAttribute("login");
    Gruppe gruppe = gruppenService.getGruppeForGithubNameById(
        username, gruppeId);
    Set<AusgabeWebobject> ausgaben = gruppe.getAusgaben().stream().map(
            e -> new AusgabeWebobject(e.getBeschreibung(), e.getBetrag().getNumber().toString(),
                e.getGlaeubiger().getGitHubName(),
                String.join(", ", e.getSchuldner().stream().map(Person::getGitHubName).toList()),
                (username.equals(e.getGlaeubiger().getGitHubName()) || e.getSchuldner().stream()
                    .map(Person::getGitHubName).toList().contains(username))))
        .collect(Collectors.toSet());
    m.addAttribute("gruppe", gruppe);
    m.addAttribute("ausgaben", ausgaben);
    AusgleichService ausgleichService = new AusgleichService();
    Set<Ueberweisung> ueberweisungen = ausgleichService.berechneAusgleichUeberweisungen(gruppe);
    m.addAttribute("ueberweisungen", ueberweisungen);
    return "gruppeDetails";
  }

}
