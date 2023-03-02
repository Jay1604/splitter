package de.hhu.propra.splitter.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import de.hhu.propra.splitter.helper.WithMockOAuth2User;
import de.hhu.propra.splitter.services.GruppenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
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
    mvc.perform(get("/")).andExpect(status().isOk());
  }

  @Test
  @DisplayName("Die private Seite ist für nicht-authentifizierte User nicht erreichbar")
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
    mvc.perform(get("/gruppe/erstellen")).andExpect(status().isOk());
  }

  @Test
  @DisplayName("Die private Seite /gruppe/erstellen ist für nicht-authentifizierte User nicht erreichbar")
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
  void test_5 () throws Exception {
    MvcResult mvcResult = mvc.perform(post("/gruppe/erstellen").param("name","gruppe1").with(csrf())).andExpect(status().is3xxRedirection()).andReturn();

    verify(gruppenService).addGruppe("nutzer1", "gruppe1");
  }

  @Test
  @DisplayName("Testen des Post Request /gruppe/erstellen mit leerem Inhalt")
  @WithMockOAuth2User(login = "nutzer1")
  void test_6 () throws Exception {
    MvcResult mvcResult = mvc.perform(post("/gruppe/erstellen").param("name","").with(csrf())).andExpect(view().name("gruppeHinzufuegen")).andReturn();


  }

}