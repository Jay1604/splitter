package de.hhu.propra.splitter.web;

import static java.util.function.Predicate.not;

import de.hhu.propra.splitter.domain.model.Gruppe;
import de.hhu.propra.splitter.domain.model.Person;
import de.hhu.propra.splitter.domain.model.Ueberweisung;
import de.hhu.propra.splitter.domain.service.AusgleichService;
import de.hhu.propra.splitter.exception.GruppeNotFound;
import de.hhu.propra.splitter.services.GruppenService;
import de.hhu.propra.splitter.web.forms.GruppeErstellenForm;
import de.hhu.propra.splitter.web.forms.GruppenSchliessenForm;
import de.hhu.propra.splitter.web.forms.PersonHinzufuegenForm;
import de.hhu.propra.splitter.web.objects.WebAusgabe;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Set;
import java.util.stream.Collectors;
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

    var geschlosseneGruppen = gruppen.stream()
        .filter(not(Gruppe::isIstOffen))
        .collect(Collectors.toSet());
    var offeneGruppen = gruppen.stream()
        .filter(Gruppe::isIstOffen)
        .collect(Collectors.toSet());

    m.addAttribute("offeneGruppen", offeneGruppen);
    m.addAttribute("geschlosseneGruppen", geschlosseneGruppen);
    return "index";
  }

  @GetMapping("/gruppe/erstellen")
  public String gruppeErstellenForm(
      GruppeErstellenForm gruppeErstellenForm
  ) {
    return "gruppeHinzufuegen";
  }

  @PostMapping("/gruppe/erstellen")
  public String gruppeHinzufuegen(
      @Valid GruppeErstellenForm gruppeErstellenForm,
      BindingResult bindingResult,
      OAuth2AuthenticationToken token,
      HttpServletResponse response
  ) {
    if (bindingResult.hasErrors()) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return "gruppeHinzufuegen";
    }
    gruppenService.addGruppe(token.getPrincipal().getAttribute("login"),
        HtmlUtils.htmlEscape(gruppeErstellenForm.name()));

    return "redirect:/";
  }

  @GetMapping("/gruppe")
  public String gruppeDetails(
      Model m,
      OAuth2AuthenticationToken token,
      @RequestParam(value = "nr") long gruppeId
  ) {
    Gruppe gruppe = gruppenService.getGruppeForGithubName(
        token.getPrincipal().getAttribute("login"), gruppeId);
    Set<WebAusgabe> history = gruppe.getAusgaben().stream().map(
        e -> new WebAusgabe(
            e.getBeschreibung(),
            e.getBetrag().getNumber().toString(),
            e.getGlaeubiger().getGitHubName(),
            String.join(
                ", ",
                e.getSchuldner().stream().map(Person::getGitHubName).toList()
            )
        )
    ).collect(Collectors.toSet());
    m.addAttribute("gruppe", gruppe);
    m.addAttribute("history", history);
    AusgleichService ausgleichService = new AusgleichService();
    Set<Ueberweisung> ueberweisungen = ausgleichService.ausgleichen(gruppe);
    m.addAttribute("ueberweisungen", ueberweisungen);
    return "gruppeDetails";
  }

  @GetMapping("gruppe/schliessen")
  public String gruppeschliessenForm(
      Model m,
      OAuth2AuthenticationToken token,
      @RequestParam(value = "nr") long gruppeId) {
    Gruppe gruppe = gruppenService.getGruppeForGithubName(
        token.getPrincipal().getAttribute("login"), gruppeId);
    if (gruppe.isIstOffen()) {
      m.addAttribute("id", gruppeId);
      return "gruppeSchliessen";
    }
    return "redirect:/gruppe?nr=" + gruppeId;
  }

  @PostMapping("gruppe/schliessen")
  public String gruppeschliessen(
      Model m,
      OAuth2AuthenticationToken token,
      @Valid
      GruppenSchliessenForm form,
      BindingResult bindingResult,
      HttpServletResponse response
  ) {
    if (bindingResult.hasErrors()) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return "gruppeSchliessen";
    }
    Gruppe gruppe = gruppenService.getGruppeForGithubName(
        token.getPrincipal().getAttribute("login"), form.id());
    if (!gruppe.isIstOffen()) {
      throw new GruppeNotFound();
    }
    gruppenService.gruppeschliessen(form.id());
    return "redirect:/gruppe?nr=" + form.id();
  }

  @GetMapping("/gruppe/nutzerHinzufuegen")
  public String nutzerHinzufuegenForm(
      Model m,
      @ModelAttribute PersonHinzufuegenForm personHinzufuegenForm,
      OAuth2AuthenticationToken token,
      @RequestParam(value = "nr") long gruppeId
  ) {
    Gruppe gruppe = gruppenService.getGruppeForGithubName(
        token.getPrincipal().getAttribute("login"), gruppeId);
    if (gruppe.isIstOffen()) {
      m.addAttribute("gruppeID", gruppeId);
      return "PersonHinzufuegen";
    }
    return "redirect:/gruppe?nr=" + gruppeId;
  }

  @PostMapping("/gruppe/nutzerHinzufuegen")
  public String nutzerHinzufuegen(
      Model m,
      OAuth2AuthenticationToken token,
      @Valid
      PersonHinzufuegenForm form,
      BindingResult bindingResult,
      HttpServletResponse response
  ) {
    if (bindingResult.hasErrors()) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return "PersonHinzufuegen";
    }
    Gruppe gruppe = gruppenService.getGruppeForGithubName(
        token.getPrincipal().getAttribute("login"), form.id());
    if (!gruppe.isIstOffen()) {
      throw new GruppeNotFound();
    }
    gruppenService.addUser(HtmlUtils.htmlEscape(form.name()), form.id());
    return "redirect:/gruppe?nr=" + form.id();
  }


}
