package de.hhu.propra.splitter.web.controller;

import de.hhu.propra.splitter.domain.services.AusgleichService;
import de.hhu.propra.splitter.services.GruppenService;
import de.hhu.propra.splitter.web.objects.UeberweisungWebobject;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UeberweisungListController {

  final GruppenService gruppenService;


  @SuppressFBWarnings("EI_EXPOSE_REP2")
  @Autowired
  public UeberweisungListController(final GruppenService gruppenService) {
    this.gruppenService = gruppenService;
  }

  @GetMapping("/meineUebersicht")
  public String ueberweisungListForPersonView(
      Model model,
      OAuth2AuthenticationToken token
  ) {
    AusgleichService ausgleichService = new AusgleichService();
    Set<UeberweisungWebobject> ueberweisungen = gruppenService
        .getGruppenForGithubName(token
            .getPrincipal()
            .getAttribute("login")
        )
        .stream()
        .flatMap(
            a -> ausgleichService
                .berechneAusgleichUeberweisungen(a)
                .stream()
                .map(
                    b -> new UeberweisungWebobject(
                        b
                            .getEmpfaenger()
                            .getGitHubName(),
                        b
                            .getBetrag()
                            .toString(),
                        b
                            .getSender()
                            .getGitHubName(),
                        a.getName()
                    )))
        .collect(Collectors.toSet());
    model.addAttribute(
        "ueberweisungen",
        ueberweisungen
    );
    return "ueberweisungList";
  }
}
