package de.hhu.propra.splitter.web;

import static java.util.function.Predicate.not;

import de.hhu.propra.splitter.domain.models.Gruppe;
import de.hhu.propra.splitter.domain.models.Person;
import de.hhu.propra.splitter.domain.models.Ueberweisung;
import de.hhu.propra.splitter.domain.services.AusgleichService;
import de.hhu.propra.splitter.exceptions.GruppeNotFound;
import de.hhu.propra.splitter.exceptions.PersonNotFound;
import de.hhu.propra.splitter.services.GruppenService;
import de.hhu.propra.splitter.web.forms.AusgabeHinzufuegenForm;
import de.hhu.propra.splitter.web.forms.GruppeErstellenForm;
import de.hhu.propra.splitter.web.forms.GruppenSchliessenForm;
import de.hhu.propra.splitter.web.forms.PersonGruppeHinzufuegenForm;
import de.hhu.propra.splitter.web.objects.AusgabeWebobject;
import de.hhu.propra.splitter.web.objects.UeberweisungWebobject;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
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
  public String gruppeListView(Model m, OAuth2AuthenticationToken auth) {
    Set<Gruppe> gruppen = gruppenService.getGruppenForGithubName(
        auth.getPrincipal().getAttribute("login"));

    var geschlosseneGruppen = gruppen.stream().filter(not(Gruppe::isOffen))
        .collect(Collectors.toSet());
    var offeneGruppen = gruppen.stream().filter(Gruppe::isOffen).collect(Collectors.toSet());

    m.addAttribute("offeneGruppen", offeneGruppen);
    m.addAttribute("geschlosseneGruppen", geschlosseneGruppen);
    return "gruppeList";
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

  //TODO: Change MoneyFormat
  @GetMapping("/gruppe")
  public String gruppeDetailsView(Model m, OAuth2AuthenticationToken token,
      @RequestParam(value = "nr") long gruppeId) {
    Gruppe gruppe = gruppenService.getGruppeForGithubNameById(
        token.getPrincipal().getAttribute("login"), gruppeId);
    Set<AusgabeWebobject> ausgaben = gruppe.getAusgaben().stream().map(
            e -> new AusgabeWebobject(e.getBeschreibung(), e.getBetrag().getNumber().toString(),
                e.getGlaeubiger().getGitHubName(),
                String.join(", ", e.getSchuldner().stream().map(Person::getGitHubName).toList())))
        .collect(Collectors.toSet());
    m.addAttribute("gruppe", gruppe);
    m.addAttribute("ausgaben", ausgaben);
    AusgleichService ausgleichService = new AusgleichService();
    Set<Ueberweisung> ueberweisungen = ausgleichService.berechneAusgleichUeberweisungen(gruppe);
    m.addAttribute("ueberweisungen", ueberweisungen);
    return "gruppeDetails";
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
      throw new GruppeNotFound();
    }
    gruppenService.addPersonToGruppe(HtmlUtils.htmlEscape(form.name()), form.id());
    return "redirect:/gruppe?nr=" + form.id();
  }

  @GetMapping("/gruppe/ausgabeHinzufuegen")
  public String ausgabeHinzufuegenView(Model m,
      @ModelAttribute AusgabeHinzufuegenForm ausgabeHinzufuegenForm,
      OAuth2AuthenticationToken token, @RequestParam(value = "nr") long gruppeId) {
    Gruppe gruppe = gruppenService.getGruppeForGithubNameById(
        token.getPrincipal().getAttribute("login"), gruppeId);
    if (gruppe.isOffen()) {
      m.addAttribute("gruppeId", gruppeId);
      m.addAttribute("mitglieder", gruppe.getMitglieder());
      return "ausgabeHinzufuegen";
    }
    return "redirect:/gruppe?nr=" + gruppeId;
  }

  @PostMapping("/gruppe/ausgabeHinzufuegen")
  public String ausgabeHinzufuegen(Model m, OAuth2AuthenticationToken token,
      @Valid AusgabeHinzufuegenForm ausgabeHinzufuegenForm, BindingResult bindingResult,
      HttpServletResponse response) {
    if (bindingResult.hasErrors()) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      if (ausgabeHinzufuegenForm.gruppeId() == null) {
        throw new GruppeNotFound();
      }
      Gruppe gruppe = gruppenService.getGruppeForGithubNameById(
          token.getPrincipal().getAttribute("login"), ausgabeHinzufuegenForm.gruppeId());
      m.addAttribute("gruppeId", ausgabeHinzufuegenForm.gruppeId());
      m.addAttribute("mitglieder", gruppe.getMitglieder());
      return "ausgabeHinzufuegen";
    }
    Gruppe gruppe = gruppenService.getGruppeForGithubNameById(
        token.getPrincipal().getAttribute("login"), ausgabeHinzufuegenForm.gruppeId());
    if (!gruppe.isOffen()) {
      throw new GruppeNotFound();
    }

    try {
      gruppenService.addAusgabe(ausgabeHinzufuegenForm.gruppeId(),
          ausgabeHinzufuegenForm.beschreibung(),
          Money.parse("EUR " + ausgabeHinzufuegenForm.betrag()),
          ausgabeHinzufuegenForm.glaeubiger(), ausgabeHinzufuegenForm.schuldner());
    } catch (PersonNotFound e) {
      bindingResult.addError(new ObjectError("FormError", "Person nicht gefunden"));
      return "ausgabeHinzufuegen";
    }
    return "redirect:/gruppe?nr=" + ausgabeHinzufuegenForm.gruppeId();
  }

  @GetMapping("/meineUebersicht")
  public String ueberweisungListForPersonView(Model model, OAuth2AuthenticationToken token) {
    AusgleichService ausgleichService = new AusgleichService();
    Set<UeberweisungWebobject> ueberweisungen = gruppenService
        .getGruppenForGithubName(token
            .getPrincipal()
            .getAttribute("login")
        ).stream()
        .flatMap(
            a -> ausgleichService.berechneAusgleichUeberweisungen(a).stream().map(
                b -> new UeberweisungWebobject(b.getEmpfaenger().getGitHubName(),
                    b.getBetrag().toString(),
                    b.getSender().getGitHubName(), a.getName()))).collect(Collectors.toSet());
    model.addAttribute("ueberweisungen", ueberweisungen);
    return "ueberweisungList";
  }


}
