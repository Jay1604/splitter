package de.hhu.propra.splitter.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import de.hhu.propra.splitter.domain.model.Gruppe;
import de.hhu.propra.splitter.exception.GruppeNotFound;
import de.hhu.propra.splitter.helper.WithMockOAuth2User;
import de.hhu.propra.splitter.services.GruppenService;
import de.hhu.propra.splitter.web.objects.WebTransaktion;
import java.util.Set;
import org.hamcrest.CoreMatchers;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest
public class ControllerTest {

  @Autowired
  MockMvc mvc;

  @MockBean
  GruppenService gruppenService;

  @Test
  @WithMockOAuth2User(login = "nutzer1")
  @DisplayName("Route / gibt 200 zurück")
  void test_1() throws Exception {
    mvc.perform(get("/"))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Die private Seite / ist für nicht-authentifizierte User nicht erreichbar")
  void test_2() throws Exception {
    MvcResult mvcResult = mvc.perform(get("/"))
        .andExpect(status().is3xxRedirection())
        .andReturn();
    assertThat(mvcResult.getResponse().getRedirectedUrl())
        .contains("oauth2/authorization/github");

  }

  @Test
  @WithMockOAuth2User(login = "nutzer1")
  @DisplayName("Route /gruppe/erstellen gibt 200 zurück")
  void test_3() throws Exception {
    mvc.perform(get("/gruppe/erstellen"))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Die private Seite /gruppe/erstellen ist für "
      + "nicht-authentifizierte User nicht erreichbar")
  void test_4() throws Exception {
    MvcResult mvcResult = mvc.perform(get("/gruppe/erstellen"))
        .andExpect(status().is3xxRedirection())
        .andReturn();
    assertThat(mvcResult.getResponse().getRedirectedUrl())
        .contains("oauth2/authorization/github");

  }

  @Test
  @DisplayName("Testen des Post Request in /gruppe/erstellen")
  @WithMockOAuth2User(login = "nutzer1")
  void test_5() throws Exception {
    mvc.perform(
        post("/gruppe/erstellen")
            .param("name", "gruppe1")
            .with(csrf())
    ).andExpect(status().is3xxRedirection()).andReturn();

    verify(gruppenService).addGruppe("nutzer1", "gruppe1");
  }

  @Test
  @DisplayName("Testen des Post Request /gruppe/erstellen mit leerem Inhalt")
  @WithMockOAuth2User(login = "nutzer1")
  void test_6() throws Exception {
    mvc.perform(
            post("/gruppe/erstellen")
                .param("name", "")
                .with(csrf())
        ).andExpect(view().name("gruppeHinzufuegen"))
        .andExpect(status().is4xxClientError())
        .andReturn();
  }

  @Test
  @WithMockOAuth2User(login = "nutzer1")
  @DisplayName("Route /gruppe?nr=<id> gibt 200 zurück")
  void test_7() throws Exception {
    Gruppe mockedResult = new Gruppe(0L, "nutzer1", "Gruppe 1");
    when(gruppenService.getGruppeForGithubName("nutzer1", 0L)).thenReturn(mockedResult);
    mvc.perform(get("/gruppe?nr=0"))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Die private Seite /gruppe?nr=<id> ist für "
      + "nicht-authentifizierte User nicht erreichbar")
  void test_8() throws Exception {
    Gruppe mockedResult = new Gruppe(0L, "nutzer1", "Gruppe 1");
    when(gruppenService.getGruppeForGithubName("nutzer1", 0L)).thenReturn(mockedResult);

    MvcResult mvcResult = mvc.perform(get("/"))
        .andExpect(status().is3xxRedirection())
        .andReturn();
    assertThat(mvcResult.getResponse().getRedirectedUrl())
        .contains("oauth2/authorization/github");

  }

  @Test
  @WithMockOAuth2User(login = "nutzer1")
  @DisplayName("Route /gruppe?nr=<id> gibt 404 zurück, wenn die Gruppe nicht existiert")
  void test_9() throws Exception {
    when(gruppenService.getGruppeForGithubName("nutzer1", 0L)).thenThrow(GruppeNotFound.class);
    mvc.perform(get("/gruppe?nr=0"))
        .andExpect(status().is(404));
  }

  @Test
  @WithMockOAuth2User(login = "nutzer1")
  @DisplayName("Route /gruppe/schliessen?nr= gibt 200 zurück")
  void test_10() throws Exception {
    Gruppe mockedResult = new Gruppe(0L, "nutzer1", "Gruppe 1");
    when(gruppenService.getGruppeForGithubName("nutzer1", 0L)).thenReturn(mockedResult);
    mvc.perform(get("/gruppe/schliessen?nr=0"))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Die private Seite /gruppe/schliessen ist für "
      + "nicht-authentifizierte User nicht erreichbar")
  void test_11() throws Exception {
    MvcResult mvcResult = mvc.perform(get("/gruppe/schliessen?nr=0"))
        .andExpect(status().is3xxRedirection())
        .andReturn();
    assertThat(mvcResult.getResponse().getRedirectedUrl())
        .contains("oauth2/authorization/github");

  }

  @Test
  @DisplayName("Testen des Post Request in /gruppe/schliessen")
  @WithMockOAuth2User(login = "nutzer1")
  void test_12() throws Exception {
    Gruppe mockedResult = new Gruppe(0L, "nutzer1", "Gruppe 1");
    when(gruppenService.getGruppeForGithubName("nutzer1", 0L)).thenReturn(mockedResult);
    mvc.perform(
        post("/gruppe/schliessen")
            .param("id", "0")
            .with(csrf())
    ).andExpect(status().is3xxRedirection()).andReturn();

    verify(gruppenService).gruppeschliessen(0L);
  }

  @Test
  @DisplayName("Testen des Post Request /gruppe/schliessen mit leerem Inhalt")
  @WithMockOAuth2User(login = "nutzer1")
  void test_13() throws Exception {
    Gruppe mockedResult = new Gruppe(0L, "nutzer1", "Gruppe 1");
    when(gruppenService.getGruppeForGithubName("nutzer1", 0L)).thenReturn(mockedResult);
    mvc.perform(
            post("/gruppe/schliessen")
                .param("id", "")
                .with(csrf())
        ).andExpect(view().name("gruppeSchliessen"))
        .andExpect(status().is4xxClientError())
        .andReturn();
  }

  @Test
  @WithMockOAuth2User(login = "nutzer1")
  @DisplayName("Route /gruppe/nutzerHinzufuegen?nr= gibt 200 zurück")
  void test_14() throws Exception {
    Gruppe mockedResult = new Gruppe(0L, "nutzer1", "Gruppe 1");
    when(gruppenService.getGruppeForGithubName("nutzer1", 0L)).thenReturn(mockedResult);
    mvc.perform(get("/gruppe/nutzerHinzufuegen?nr=0"))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Die private Seite /gruppe/nutzerHinzufuegen ist für "
      + "nicht-authentifizierte User nicht erreichbar")
  void test_15() throws Exception {
    MvcResult mvcResult = mvc.perform(get("/gruppe/nutzerHinzufuegen?nr=0"))
        .andExpect(status().is3xxRedirection())
        .andReturn();
    assertThat(mvcResult.getResponse().getRedirectedUrl())
        .contains("oauth2/authorization/github");

  }

  @Test
  @DisplayName("Testen des Post Request in /gruppe/nutzerHinzufuegen")
  @WithMockOAuth2User(login = "nutzer1")
  void test_16() throws Exception {
    Gruppe mockedResult = new Gruppe(0L, "nutzer1", "Gruppe 1");
    when(gruppenService.getGruppeForGithubName("nutzer1", 0L)).thenReturn(mockedResult);
    mvc.perform(
        post("/gruppe/nutzerHinzufuegen")
            .param("id", "0")
            .param("name", "nutzer2")
            .with(csrf())
    ).andExpect(status().is3xxRedirection()).andReturn();

    verify(gruppenService).addUser("nutzer2", 0L);
  }

  @Test
  @DisplayName("Testen des Post Request /gruppe/nutzerHinzufuegen mit leerem Inhalt")
  @WithMockOAuth2User(login = "nutzer1")
  void test_17() throws Exception {
    Gruppe mockedResult = new Gruppe(0L, "nutzer1", "Gruppe 1");
    when(gruppenService.getGruppeForGithubName("nutzer1", 0L)).thenReturn(mockedResult);
    mvc.perform(
            post("/gruppe/nutzerHinzufuegen")
                .param("id", "")
                .param("name", "")
                .with(csrf())
        ).andExpect(view().name("PersonHinzufuegen"))
        .andExpect(status().is4xxClientError())
        .andReturn();
  }

  @Test
  @DisplayName("Testen des Post Request /gruppe/nutzerHinzufuegen wenn Gruppe geschlossen")
  @WithMockOAuth2User(login = "nutzer1")
  void test_18() throws Exception {
    Gruppe mockedResult = new Gruppe(0L, "nutzer1", "Gruppe 1");
    mockedResult.setIstOffen(false);
    when(gruppenService.getGruppeForGithubName("nutzer1", 0L)).thenReturn(mockedResult);
    mvc.perform(
            post("/gruppe/nutzerHinzufuegen")
                .param("id", "0")
                .param("name", "Nutzer2")
                .with(csrf())
        ).andExpect(status().is4xxClientError())
        .andReturn();
  }

  //region TransaktionsTests
  @Test
  @WithMockOAuth2User(login = "nutzer1")
  @DisplayName("Route /gruppe/neueTransaktion?nr= gibt 200 zurück")
  void test_19() throws Exception {
    Gruppe mockedResult = new Gruppe(0L, "nutzer1", "Gruppe 1");
    when(gruppenService.getGruppeForGithubName("nutzer1", 0L)).thenReturn(mockedResult);
    mvc.perform(get("/gruppe/neueTransaktion?nr=0"))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Die private Seite /gruppe/neueTransaktion ist für "
      + "nicht-authentifizierte User nicht erreichbar")
  void test_20() throws Exception {
    MvcResult mvcResult = mvc.perform(get("/gruppe/neueTransaktion?nr=0"))
        .andExpect(status().is3xxRedirection())
        .andReturn();
    assertThat(mvcResult.getResponse().getRedirectedUrl())
        .contains("oauth2/authorization/github");

  }

  @Test
  @DisplayName("Testen des Post Request in /gruppe/neueTransaktion")
  @WithMockOAuth2User(login = "nutzer1")
  void test_21() throws Exception {
    Gruppe mockedResult = new Gruppe(0L, "nutzer1", "Gruppe 1");
    mockedResult.addMitglied("nutzer2");
    when(gruppenService.getGruppeForGithubName("nutzer1", 0L)).thenReturn(mockedResult);
    mvc.perform(
        post("/gruppe/neueTransaktion")
            .param("id", "0")
            .param("betrag", "10.02")
            .param("aktivitaet", "test")
            .param("glaeubiger", "nutzer1")
            .param("schuldner", "nutzer1")
            .param("schuldner", "nutzer2")
            .with(csrf())
    ).andExpect(status().is3xxRedirection()).andReturn();

    verify(gruppenService).addTransaktion(0L, "test", Money.of(10.02, "EUR"), "nutzer1",
        Set.of("nutzer1", "nutzer2"));
  }


  @Test
  @DisplayName("Testen des Post Request in /gruppe/neueTransaktion ohne id")
  @WithMockOAuth2User(login = "nutzer1")
  void test_22() throws Exception {
    Gruppe mockedResult = new Gruppe(0L, "nutzer1", "Gruppe 1");
    mockedResult.addMitglied("nutzer2");
    when(gruppenService.getGruppeForGithubName("nutzer1", 0L)).thenReturn(mockedResult);
    mvc.perform(
            post("/gruppe/neueTransaktion")
                .param("id", "")
                .param("betrag", "10.02")
                .param("aktivitaet", "test")
                .param("glaeubiger", "nutzer1")
                .param("schuldner", "nutzer1")
                .param("schuldner", "nutzer2")
                .with(csrf())
        ).andExpect(status().is4xxClientError())
        .andReturn();
  }

  @Test
  @DisplayName("Testen des Post Request in /gruppe/neueTransaktion ohne betrag")
  @WithMockOAuth2User(login = "nutzer1")
  void test_23() throws Exception {
    Gruppe mockedResult = new Gruppe(0L, "nutzer1", "Gruppe 1");
    mockedResult.addMitglied("nutzer2");
    when(gruppenService.getGruppeForGithubName("nutzer1", 0L)).thenReturn(mockedResult);
    mvc.perform(
            post("/gruppe/neueTransaktion")
                .param("id", "0")
                .param("betrag", "")
                .param("aktivitaet", "test")
                .param("glaeubiger", "nutzer1")
                .param("schuldner", "nutzer1")
                .param("schuldner", "nutzer2")
                .with(csrf())
        ).andExpect(status().is4xxClientError())
        .andReturn();
  }

  @Test
  @DisplayName("Testen des Post Request in /gruppe/neueTransaktion ohne aktivitaet")
  @WithMockOAuth2User(login = "nutzer1")
  void test_24() throws Exception {
    Gruppe mockedResult = new Gruppe(0L, "nutzer1", "Gruppe 1");
    mockedResult.addMitglied("nutzer2");
    when(gruppenService.getGruppeForGithubName("nutzer1", 0L)).thenReturn(mockedResult);
    mvc.perform(
            post("/gruppe/neueTransaktion")
                .param("id", "0")
                .param("betrag", "10.02")
                .param("aktivitaet", "")
                .param("glaeubiger", "nutzer1")
                .param("schuldner", "nutzer1")
                .param("schuldner", "nutzer2")
                .with(csrf())
        ).andExpect(status().is4xxClientError())
        .andReturn();
  }

  @Test
  @DisplayName("Testen des Post Request in /gruppe/neueTransaktion ohne glaeubiger")
  @WithMockOAuth2User(login = "nutzer1")
  void test_25() throws Exception {
    Gruppe mockedResult = new Gruppe(0L, "nutzer1", "Gruppe 1");
    mockedResult.addMitglied("nutzer2");
    when(gruppenService.getGruppeForGithubName("nutzer1", 0L)).thenReturn(mockedResult);
    mvc.perform(
            post("/gruppe/neueTransaktion")
                .param("id", "0")
                .param("betrag", "10.02")
                .param("aktivitaet", "test")
                .param("glaeubiger", "")
                .param("schuldner", "nutzer1")
                .param("schuldner", "nutzer2")
                .with(csrf())
        ).andExpect(status().is4xxClientError())
        .andReturn();
  }

  @Test
  @DisplayName("Testen des Post Request in /gruppe/neueTransaktion ohne schuldner")
  @WithMockOAuth2User(login = "nutzer1")
  void test_26() throws Exception {
    Gruppe mockedResult = new Gruppe(0L, "nutzer1", "Gruppe 1");
    mockedResult.addMitglied("nutzer2");
    when(gruppenService.getGruppeForGithubName("nutzer1", 0L)).thenReturn(mockedResult);
    mvc.perform(
            post("/gruppe/neueTransaktion")
                .param("id", "0")
                .param("betrag", "")
                .param("aktivitaet", "test")
                .param("glaeubiger", "nutzer1")
                .param("schuldner", "")
                .with(csrf())
        ).andExpect(status().is4xxClientError())
        .andReturn();
  }

  @Test
  @WithMockOAuth2User(login = "nutzer1")
  @DisplayName("Route /nutzer/uebersicht gibt 200 zurück")
  void test_27() throws Exception {
    Gruppe mockedResult = new Gruppe(0L, "nutzer1", "Gruppe 1");
    when(gruppenService.getGruppeForGithubName("nutzer1", 0L)).thenReturn(mockedResult);
    mvc.perform(get("/nutzer/uebersicht"))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("Die private Seite /nutzer/uebersicht ist für "
      + "nicht-authentifizierte User nicht erreichbar")
  void test_28() throws Exception {
    MvcResult mvcResult = mvc.perform(get("/nutzer/uebersicht"))
        .andExpect(status().is3xxRedirection())
        .andReturn();
    assertThat(mvcResult.getResponse().getRedirectedUrl())
        .contains("oauth2/authorization/github");

  }

  @Test
  @DisplayName("Die  Seite /nutzer/uebersicht stellt alle Transaktion dar")
  @WithMockOAuth2User(login = "nutzer1")
  void test_29() throws Exception {
    Gruppe mockGruppe = new Gruppe(0L, "nutzer1", "Gruppe 1");
    mockGruppe.addMitglied("nutzer2");
    mockGruppe.addAusgabe("test", Money.of(10, "EUR"), "nutzer1", Set.of("nutzer1", "nutzer2"));
    Set<Gruppe> gruppen = Set.of(mockGruppe);
    when(gruppenService.getGruppenForGithubName("nutzer1")).thenReturn(gruppen);
    mvc.perform(get("/nutzer/uebersicht"))
        .andExpect(status().isOk())
        .andExpect(content().string(CoreMatchers.containsString("5")));
  }

  //endregion
}