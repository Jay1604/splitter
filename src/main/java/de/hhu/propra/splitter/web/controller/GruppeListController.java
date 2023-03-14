package de.hhu.propra.splitter.web.controller;

import static java.util.function.Predicate.not;

import de.hhu.propra.splitter.domain.models.Gruppe;
import de.hhu.propra.splitter.services.GruppenService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GruppeListController {

  final GruppenService gruppenService;


  @SuppressFBWarnings("EI_EXPOSE_REP2")
  @Autowired
  public GruppeListController(final GruppenService gruppenService) {
    this.gruppenService = gruppenService;
  }

  @GetMapping("/")
  public String gruppeListView(
      Model m,
      OAuth2AuthenticationToken auth
  ) {
    List<Gruppe> gruppen = gruppenService.getGruppenForGithubName(
        auth
            .getPrincipal()
            .getAttribute("login")).stream().sorted().toList();

    var geschlosseneGruppen = gruppen
        .stream()
        .filter(not(Gruppe::isOffen))
        .toList();
    var offeneGruppen = gruppen
        .stream()
        .filter(Gruppe::isOffen)
        .toList();

    m.addAttribute(
        "offeneGruppen",
        offeneGruppen
    );
    m.addAttribute(
        "geschlosseneGruppen",
        geschlosseneGruppen
    );
    return "gruppeList";
  }
}
