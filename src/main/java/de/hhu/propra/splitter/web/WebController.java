package de.hhu.propra.splitter.web;

import static java.util.function.Predicate.not;

import de.hhu.propra.splitter.domain.model.Gruppe;
import de.hhu.propra.splitter.domain.model.Person;
import de.hhu.propra.splitter.services.GruppenService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {


  final GruppenService gruppenService;

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  @Autowired
  public WebController(final GruppenService gruppenService) {
    this.gruppenService = gruppenService;
    Gruppe gruppe1 = new Gruppe(1L, new Person("..."), "Ausflug");
    Gruppe gruppe2 = new Gruppe(2L, new Person("..."), "Kein Ausflug mehr");
    gruppe2.setIstOffen(false);
    gruppenService.addGruppe(gruppe1);
    gruppenService.addGruppe(gruppe2);
  }

  @GetMapping("/")
  public String index(Model m, OAuth2AuthenticationToken auth) {
    Set<Gruppe> gruppen = gruppenService.getGruppenForGithubName(
        auth.getPrincipal().getAttribute("login"));

    var geschlosseneGruppen = gruppen.stream().filter(not(Gruppe::isIstOffen))
        .collect(Collectors.toSet());
    var offeneGruppen = gruppen.stream().filter(Gruppe::isIstOffen).collect(Collectors.toSet());

    m.addAttribute("offeneGruppen", offeneGruppen);
    m.addAttribute("geschlosseneGruppen", geschlosseneGruppen);
    return "index";
  }


}
