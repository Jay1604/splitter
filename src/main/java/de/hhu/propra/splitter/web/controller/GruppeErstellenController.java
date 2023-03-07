package de.hhu.propra.splitter.web.controller;

import de.hhu.propra.splitter.services.GruppenService;
import de.hhu.propra.splitter.web.forms.GruppeErstellenForm;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.util.HtmlUtils;

@Controller
public class GruppeErstellenController {

  final GruppenService gruppenService;


  @SuppressFBWarnings("EI_EXPOSE_REP2")
  @Autowired
  public GruppeErstellenController(final GruppenService gruppenService) {
    this.gruppenService = gruppenService;
  }

  @GetMapping("/gruppe/erstellen")
  public String gruppeErstellenView(GruppeErstellenForm gruppeErstellenForm) {
    return "gruppeErstellen";
  }

  @PostMapping("/gruppe/erstellen")
  public String gruppeErstellen(@Valid GruppeErstellenForm gruppeErstellenForm,
      BindingResult bindingResult, OAuth2AuthenticationToken token, HttpServletResponse response) {
    if (bindingResult.hasErrors()) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return "gruppeErstellen";
    }
    gruppenService.addGruppe(token.getPrincipal().getAttribute("login"),
        HtmlUtils.htmlEscape(gruppeErstellenForm.name()));

    return "redirect:/";
  }


}
