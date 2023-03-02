package de.hhu.propra.splitter.web;

import static java.util.function.Predicate.not;

import de.hhu.propra.splitter.domain.model.Gruppe;
import de.hhu.propra.splitter.domain.model.Person;
import de.hhu.propra.splitter.services.GruppenService;
import de.hhu.propra.splitter.web.forms.GruppeErstellenForm;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.util.HtmlUtils;

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
    Set<Gruppe> gruppen = gruppenService.getGruppenForGithubName(
        auth.getPrincipal().getAttribute("login"));

    var geschlosseneGruppen = gruppen.stream().filter(not(Gruppe::isIstOffen))
        .collect(Collectors.toSet());
    var offeneGruppen = gruppen.stream().filter(Gruppe::isIstOffen).collect(Collectors.toSet());

    m.addAttribute("offeneGruppen", offeneGruppen);
    m.addAttribute("geschlosseneGruppen", geschlosseneGruppen);
    return "index";
  }

  @GetMapping("/gruppe/erstellen")
  public String gruppeerstellen(){
    return "gruppeHinzufuegen";
  }
@PostMapping("/gruppe/erstellen")
  public String gruppeHinzufuegen(Model m, @Valid GruppeErstellenForm form, BindingResult bindingResult, OAuth2AuthenticationToken token){
    if(bindingResult.hasErrors()){
      return "gruppeHinzufuegen";
    }
    gruppenService.addGruppe(token.getPrincipal().getAttribute("login"), HtmlUtils.htmlEscape(form.name()));

    return "redirect:/";
}










}
