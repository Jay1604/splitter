package de.hhu.propra.splitter.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import de.hhu.propra.splitter.domain.model.Gruppe;
import de.hhu.propra.splitter.exception.GruppeNotFound;
import de.hhu.propra.splitter.helper.WithMockOAuth2User;
import de.hhu.propra.splitter.services.GruppenService;
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
  @DisplayName("Testen des Post Request in /gruppe/schliessen")
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
}