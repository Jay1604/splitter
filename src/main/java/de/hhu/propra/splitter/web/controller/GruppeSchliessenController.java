package de.hhu.propra.splitter.web.controller;

import de.hhu.propra.splitter.domain.models.Gruppe;
import de.hhu.propra.splitter.exceptions.GruppeNotFound;
import de.hhu.propra.splitter.services.GruppenService;
import de.hhu.propra.splitter.web.forms.GruppenSchliessenForm;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GruppeSchliessenController {

  final GruppenService gruppenService;


  @SuppressFBWarnings("EI_EXPOSE_REP2")
  @Autowired
  public GruppeSchliessenController(final GruppenService gruppenService) {
    this.gruppenService = gruppenService;
  }

  @GetMapping("gruppe/schliessen")
  public String gruppeSchliessenView(Model m, OAuth2AuthenticationToken token,
      @RequestParam(value = "nr") long gruppeId) {
    Gruppe gruppe = gruppenService.getGruppeForGithubNameById(
        token.getPrincipal().getAttribute("login"), gruppeId);
    if (gruppe.isOffen()) {
      m.addAttribute("id", gruppeId);
      return "gruppeSchliessen";
    }
    return "redirect:/gruppe?nr=" + gruppeId;
  }

  @PostMapping("gruppe/schliessen")
  public String gruppeSchliessen(Model m, OAuth2AuthenticationToken token,
      @Valid GruppenSchliessenForm form, BindingResult bindingResult,
      HttpServletResponse response) {
    if (bindingResult.hasErrors()) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return "gruppeSchliessen";
    }
    Gruppe gruppe = gruppenService.getGruppeForGithubNameById(
        token.getPrincipal().getAttribute("login"), form.id());
    if (!gruppe.isOffen()) {
      throw new GruppeNotFound();
    }
    gruppenService.schliesseGruppe(form.id());
    return "redirect:/gruppe?nr=" + form.id();
  }
}
