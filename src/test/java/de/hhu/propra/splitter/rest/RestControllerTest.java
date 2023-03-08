package de.hhu.propra.splitter.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.hhu.propra.splitter.config.SecurityConfig;
import de.hhu.propra.splitter.services.GruppenService;
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


  public void test_1(
  ) throws Exception {
    when(gruppenService.addGruppe(anyString(), anyString())).thenReturn(0L);

    mvc.perform(post("/api/gruppen").contentType(MediaType.APPLICATION_JSON).content(
                "{\"name\" : \"Tour 2023\", \"personen\" : [\"Mick\", \"Keith\", \"Ronnie\"] }"))
        .andExpect(status().is(201));
    verify(gruppenService).addGruppe("Mick", "Tour 2023");
    verify(gruppenService).addPersonToGruppe("Keith", 0L);
    verify(gruppenService).addPersonToGruppe("Ronnie", 0L);

  }




}
