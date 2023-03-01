package de.hhu.propra.splitter.web;

import de.hhu.propra.splitter.domain.model.Gruppe;
import de.hhu.propra.splitter.services.GruppenService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Set;
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
  }

  @GetMapping("/")
  public String index(Model m, OAuth2AuthenticationToken auth) {
    Set<Gruppe> geschlosseneGruppen = gruppenService.getGruppenForGithubName(
        auth.getPrincipal().getAttribute("login"));

    System.out.println(geschlosseneGruppen);

    return "index";
  }


}
