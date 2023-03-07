package de.hhu.propra.splitter.web.controller;

import de.hhu.propra.splitter.domain.models.Gruppe;
import de.hhu.propra.splitter.exceptions.GruppeNotFoundException;
import de.hhu.propra.splitter.services.GruppenService;
import de.hhu.propra.splitter.web.forms.PersonGruppeHinzufuegenForm;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.HtmlUtils;

@Controller
public class PersonGruppeHinzufuegenController {

  final GruppenService gruppenService;


  @SuppressFBWarnings("EI_EXPOSE_REP2")
  @Autowired
  public PersonGruppeHinzufuegenController(final GruppenService gruppenService) {
    this.gruppenService = gruppenService;
  }

  @GetMapping("/gruppe/personHinzufuegen")
  public String personGruppeHinzufuegenView(Model m,
      @ModelAttribute PersonGruppeHinzufuegenForm personGruppeHinzufuegenForm,
      OAuth2AuthenticationToken token,
      @RequestParam(value = "nr") long gruppeId) {
    Gruppe gruppe = gruppenService.getGruppeForGithubNameById(
        token.getPrincipal().getAttribute("login"), gruppeId);
    if (gruppe.isOffen() && gruppe.getAusgaben().size() == 0) {
      m.addAttribute("gruppeID", gruppeId);
      return "personGruppeHinzufuegen";
    }
    return "redirect:/gruppe?nr=" + gruppeId;
  }

  @PostMapping("/gruppe/personHinzufuegen")
  public String personGruppeHinzufuegen(Model m, OAuth2AuthenticationToken token,
      @Valid PersonGruppeHinzufuegenForm form, BindingResult bindingResult,
      HttpServletResponse response) {
    if (bindingResult.hasErrors()) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return "personGruppeHinzufuegen";
    }
    Gruppe gruppe = gruppenService.getGruppeForGithubNameById(
        token.getPrincipal().getAttribute("login"), form.id());
    if (!gruppe.isOffen() && gruppe.getAusgaben().size() == 0) {
      throw new GruppeNotFoundException();
    }
    gruppenService.addPersonToGruppe(HtmlUtils.htmlEscape(form.name()), form.id());
    return "redirect:/gruppe?nr=" + form.id();
  }

}
