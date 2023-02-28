package de.hhu.propra.splitter.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.hhu.propra.splitter.helper.WithMockOAuth2User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
public class ControllerTest {

  @Autowired
  MockMvc mvc;

  @Test
  @WithMockOAuth2User(login = "nutzer1")
  @DisplayName("Route / gibt 200 zurück")
  void test_1() throws Exception {
    mvc.perform(get("/")).andExpect(status().isOk());
  }
}
