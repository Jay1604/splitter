package de.hhu.propra.splitter.rest;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.hhu.propra.splitter.config.SecurityConfig;
import de.hhu.propra.splitter.factories.GruppeFactory;
import de.hhu.propra.splitter.services.GruppenService;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@ImportAutoConfiguration(classes = SecurityConfig.class)
public class RestControllerTest {

  @Autowired
  MockMvc mvc;

  @MockBean
  GruppenService gruppenService;


  @Test
  @DisplayName("Gruppe erstellen mit JSON")
  void test_1(
  ) throws Exception {
    when(gruppenService.addGruppe(anyString(), anyString())).thenReturn(0L);

    mvc.perform(post("/api/gruppen").contentType(MediaType.APPLICATION_JSON).content(
            "{\"name\" : \"Tour 2023\", \"personen\" : [\"Mick\", \"Keith\", \"Ronnie\"] }"))
        .andExpect(status().is(201));
    verify(gruppenService).addGruppe("Mick", "Tour 2023");
    verify(gruppenService).addPersonToGruppe("Keith", 0L);
    verify(gruppenService).addPersonToGruppe("Ronnie", 0L);

  }

  @Test
  @DisplayName("Wenn Gruppe-Request unvollständig, dann 400")
  void test_2() throws Exception {
    mvc.perform(post("/api/gruppen").contentType(MediaType.APPLICATION_JSON).content(
            "{\"personen\" : [\"Mick\", \"Keith\", \"Ronnie\"] }"))
        .andExpect(status().is(400));
  }

  @Test
  @DisplayName("Existierende Gruppen für Nutzer ausgeben mit JSON")
  void test_3(
  ) throws Exception {
    when(gruppenService.getGruppenForGithubName("nutzer1")).thenReturn(Set.of(
        new GruppeFactory().withId(0L).withName("test").withMitglieder(Set.of("nutzer1")).build()));

    mvc.perform(get("/api/user/nutzer1/gruppen"))
        .andExpect(status().is(200)).andExpect(
            content().json("[{\"gruppe\":\"0\",\"name\":\"test\",\"personen\":[\"nutzer1\"]}]"));
  }

  @Test
  @DisplayName("Wenn keine Gruppen oder die Person unbekannt, dann leeres Array")
  void test_4() throws Exception {
    mvc.perform(get("/api/user/nutzer1/gruppen"))
        .andExpect(status().is(200)).andExpect(content().json("[]"));

  }

}
