package de.hhu.propra.splitter.web.controller;

import de.hhu.propra.splitter.domain.models.Gruppe;
import de.hhu.propra.splitter.exceptions.GruppeGeschlossenException;
import de.hhu.propra.splitter.exceptions.GruppeNotFoundException;
import de.hhu.propra.splitter.exceptions.PersonNotFoundException;
import de.hhu.propra.splitter.services.GruppenService;
import de.hhu.propra.splitter.web.forms.AusgabeHinzufuegenForm;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.HtmlUtils;

@Controller
public class AusgabeHinzufuegenController {

  final GruppenService gruppenService;


  @SuppressFBWarnings("EI_EXPOSE_REP2")
  @Autowired
  public AusgabeHinzufuegenController(final GruppenService gruppenService) {
    this.gruppenService = gruppenService;
  }

  @GetMapping("/gruppe/ausgabeHinzufuegen")
  public String ausgabeHinzufuegenView(
      Model m,
      OAuth2AuthenticationToken token,
      @RequestParam(value = "nr") long gruppeId
  ) {
    m.addAttribute(
        "ausgabeHinzufuegenForm",
        new AusgabeHinzufuegenForm(
            null,
            null,
            null,
            token
                .getPrincipal()
                .getAttribute("login"),
            null
        )
    );
    Gruppe gruppe = gruppenService.getGruppeForGithubNameById(
        token
            .getPrincipal()
            .getAttribute("login"),
        gruppeId
    );
    if (gruppe.isOffen()) {
      m.addAttribute(
          "gruppeId",
          gruppeId
      );
      m.addAttribute(
          "mitglieder",
          gruppe
              .getMitglieder()
              .stream()
              .sorted()
              .toList()
      );
      return "ausgabeHinzufuegen";
    }
    return "redirect:/gruppe?nr=" + gruppeId;
  }

  @PostMapping("/gruppe/ausgabeHinzufuegen")
  public String ausgabeHinzufuegen(
      Model m,
      OAuth2AuthenticationToken token,
      @Valid AusgabeHinzufuegenForm ausgabeHinzufuegenForm,
      BindingResult bindingResult,
      HttpServletResponse response
  ) {
    if (bindingResult.hasErrors()) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      if (ausgabeHinzufuegenForm.gruppeId() == null) {
        throw new GruppeNotFoundException();
      }
      Gruppe gruppe = gruppenService.getGruppeForGithubNameById(
          token
              .getPrincipal()
              .getAttribute("login"),
          ausgabeHinzufuegenForm.gruppeId()
      );
      m.addAttribute(
          "gruppeId",
          ausgabeHinzufuegenForm.gruppeId()
      );
      m.addAttribute(
          "mitglieder",
          gruppe
              .getMitglieder()
              .stream()
              .sorted()
              .toList()
      );
      return "ausgabeHinzufuegen";
    }
    Gruppe gruppe = gruppenService.getGruppeForGithubNameById(
        token
            .getPrincipal()
            .getAttribute("login"),
        ausgabeHinzufuegenForm.gruppeId()
    );
    if (!gruppe.isOffen()) {
      throw new GruppeGeschlossenException();
    }

    gruppenService.addAusgabe(
        ausgabeHinzufuegenForm.gruppeId(),
        HtmlUtils.htmlEscape(ausgabeHinzufuegenForm.beschreibung()),
        Money.parse("EUR " + ausgabeHinzufuegenForm.betrag()),
        ausgabeHinzufuegenForm.glaeubiger(),
        ausgabeHinzufuegenForm.schuldner()
    );

    return "redirect:/gruppe?nr=" + ausgabeHinzufuegenForm.gruppeId();
  }

  @ExceptionHandler(GruppeGeschlossenException.class)
  public ResponseEntity<String> handleGruppeGeschlossenException() {
    return new ResponseEntity<String>(
        "Gruppe ist geschlossen",
        HttpStatus.BAD_REQUEST
    );
  }

  @ExceptionHandler(PersonNotFoundException.class)
  public ResponseEntity<String> handlePersonNotFoundException() {
    return new ResponseEntity<String>(
        "Person nicht gefunden",
        HttpStatus.BAD_REQUEST
    );
  }
}
