package de.hhu.propra.splitter.rest;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.hhu.propra.splitter.config.SecurityConfig;
import de.hhu.propra.splitter.domain.models.Gruppe;
import de.hhu.propra.splitter.exceptions.GruppeNotFoundException;
import de.hhu.propra.splitter.factories.AusgabeFactory;
import de.hhu.propra.splitter.factories.GruppeFactory;
import de.hhu.propra.splitter.services.GruppenService;
import java.util.Set;
import org.javamoney.moneta.Money;
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

  @Test
  @DisplayName("Details für existierende Gruppe ausgeben")
  void test_5(
  ) throws Exception {
    when(gruppenService.getGruppeById("0")).thenReturn(
        new GruppeFactory()
            .withId(0L)
            .withName("Tour 2023")
            .withMitglieder(Set.of("Mick", "Keith", "Ronnie"))
            .withAusgaben(Set.of(
                new AusgabeFactory()
                    .withBeschreibung("Black Paint")
                    .withBetrag(Money.of(25.99, "EUR"))
                    .withGlaeubiger("Keith")
                    .withSchuldner(Set.of("Mick", "Keith", "Ronnie"))
                    .build()
            )).build());

    mvc.perform(get("/api/gruppen/0"))
        .andExpect(status().is(200)).andExpect(
            content().json(
                "{\"gruppe\" : \"0\", \"name\" : \"Tour 2023\","
                    + " \"personen\" : [\"Mick\", \"Keith\", \"Ronnie\"], "
                    + " \"geschlossen\": false, "
                    + " \"ausgaben\" : [{\"grund\": \"Black Paint\","
                    + " \"glaeubiger\": \"Keith\", \"cent\" : 2599,"
                    + " \"schuldner\" : [\"Mick\", \"Keith\", \"Ronnie\"]}]}"));
  }

  @Test
  @DisplayName("Wenn keine Gruppe existiert, dann 404")
  void test_6() throws Exception {
    when(gruppenService.getGruppeById("0")).thenThrow(GruppeNotFoundException.class);

    mvc.perform(get("/api/gruppen/0"))
        .andExpect(status().is(404));

  }

  @Test
  @DisplayName("Schliese Gruppe, wenn alles korrekt")
  void test_7() throws Exception {
    when(gruppenService.getGruppeById("0"))
        .thenReturn(new GruppeFactory().withId(0).build());
    mvc.perform(post("/api/gruppen/0/schliessen"))
        .andExpect(status().is(200));

    verify(gruppenService).schliesseGruppe(0L);
  }

  @Test
  @DisplayName("Schliese Gruppe gibt 404, wenn Gruppe nicht existiert")
  void test_8() throws Exception {
    when(gruppenService.getGruppeById("0"))
        .thenReturn(new GruppeFactory().withId(0).build());
    doThrow(GruppeNotFoundException.class).when(gruppenService).schliesseGruppe(0);

    mvc.perform(post("/api/gruppen/0/schliessen"))
        .andExpect(status().is(404));

  }

  @Test
  @DisplayName("Füge Ausgabe zu Gruppe hinzu, wenn alles korrekt")
  void test_9() throws Exception {
    Gruppe mockedResult = new GruppeFactory()
        .withId(0L)
        .withName("Tour 2023")
        .withMitglieder(Set.of("Mick", "Keith", "Ronnie"))
        .build();

    when(gruppenService.getGruppeById("0")).thenReturn(mockedResult);

    mvc.perform(post("/api/gruppen/0/auslagen")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"grund\": \"Black Paint\", \"glaeubiger\": \"Keith\","
                + "\"cent\" : 2599, \"schuldner\" : [\"Mick\", \"Keith\", \"Ronnie\"]}"))
        .andExpect(status().is(201));

    verify(gruppenService).addAusgabe(
        0L,
        "Black Paint",
        Money.of(2599, "EUR").divide(100),
        "Keith",
        Set.of("Mick", "Keith", "Ronnie")
    );
  }

  @Test
  @DisplayName("Füge Ausgabe zu Gruppe hinzu gibt 404, wenn Gruppe nicht existiert")
  void test_10() throws Exception {
    when(gruppenService.getGruppeById("0")).thenThrow(GruppeNotFoundException.class);

    mvc.perform(post("/api/gruppen/0/auslagen")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"grund\": \"Black Paint\", \"glaeubiger\": \"Keith\","
                + "\"cent\" : 2599, \"schuldner\" : [\"Mick\", \"Keith\", \"Ronnie\"]}"))
        .andExpect(status().is(404));

  }

  @Test
  @DisplayName("Füge Ausgabe zu Gruppe hinzu gibt 409, wenn Gruppe geschlossen")
  void test_11() throws Exception {
    Gruppe mockedResult = new GruppeFactory()
        .withId(0L)
        .withName("Tour 2023")
        .withIstOffen(false)
        .withMitglieder(Set.of("Mick", "Keith", "Ronnie"))
        .build();

    when(gruppenService.getGruppeById("0")).thenReturn(mockedResult);

    mvc.perform(post("/api/gruppen/0/auslagen")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"grund\": \"Black Paint\", \"glaeubiger\": \"Keith\","
                + "\"cent\" : 2599, \"schuldner\" : [\"Mick\", \"Keith\", \"Ronnie\"]}"))
        .andExpect(status().is(409));
  }

  @Test
  @DisplayName("Füge Ausgabe zu Gruppe hinzu gibt 400, wenn Request Body fehlerhaft (Grund)")
  void test_12() throws Exception {
    mvc.perform(post("/api/gruppen/0/auslagen")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"glaeubiger\": \"Keith\","
                + "\"cent\" : 2599, \"schuldner\" : [\"Mick\", \"Keith\", \"Ronnie\"]}"))
        .andExpect(status().is(400));
  }

  @Test
  @DisplayName("Füge Ausgabe zu Gruppe hinzu gibt 400, wenn Request Body fehlerhaft (Glaeubiger)")
  void test_13() throws Exception {
    mvc.perform(post("/api/gruppen/0/auslagen")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"grund\": \"Black Paint\","
                + "\"cent\" : 2599, \"schuldner\" : [\"Mick\", \"Keith\", \"Ronnie\"]}"))
        .andExpect(status().is(400));
  }

  @Test
  @DisplayName("Füge Ausgabe zu Gruppe hinzu gibt 400, wenn Request Body fehlerhaft (Cent)")
  void test_14() throws Exception {
    mvc.perform(post("/api/gruppen/0/auslagen")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"grund\": \"Black Paint\", \"glaeubiger\": \"Keith\","
                + "\"schuldner\" : [\"Mick\", \"Keith\", \"Ronnie\"]}"))
        .andExpect(status().is(400));
  }

  @Test
  @DisplayName("Füge Ausgabe zu Gruppe hinzu gibt 400, wenn Request Body fehlerhaft (Schuldner)")
  void test_15() throws Exception {
    mvc.perform(post("/api/gruppen/0/auslagen")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"grund\": \"Black Paint\", \"glaeubiger\": \"Keith\","
                + "\"cent\" : 2599}"))
        .andExpect(status().is(400));
  }

  @Test
  @DisplayName("Ausgleich Ueberweisungen für Gruppe ausgeben")
  void test_16() throws Exception {
    when(gruppenService.getGruppeById("0")).thenReturn(
        new GruppeFactory()
            .withId(0L)
            .withName("Tour 2023")
            .withMitglieder(Set.of("Mick", "Keith", "Ronnie"))
            .withAusgaben(Set.of(
                new AusgabeFactory()
                    .withBeschreibung("Black Paint")
                    .withBetrag(Money.of(25.98, "EUR"))
                    .withGlaeubiger("Keith")
                    .withSchuldner(Set.of("Mick", "Keith", "Ronnie"))
                    .build()
            )).build());

    mvc.perform(get("/api/gruppen/0/ausgleich"))
        .andExpect(status().is(200)).andExpect(
            content().json(
                "[{\"von\" : \"Mick\","
                    + "\"an\" : \"Keith\","
                    + "\"cents\" : 866},"
                    + "{\"von\" : \"Ronnie\","
                    + "\"an\" : \"Keith\","
                    + "\"cents\" : 866}]"));
  }

  @Test
  @DisplayName("Ausgleich Ueberweisungen für Gruppe ausgeben gibt 404, wenn Gruppe nicht existiert")
  void test_17() throws Exception {
    when(gruppenService.getGruppeById("0")).thenThrow(GruppeNotFoundException.class);

    mvc.perform(get("/api/gruppen/0/ausgleich"))
        .andExpect(status().is(404));
  }
}
