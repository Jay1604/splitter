package de.hhu.propra.splitter.services;

import static org.assertj.core.api.Assertions.assertThat;

import de.hhu.propra.splitter.domain.models.Gruppe;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class GruppenServiceTest {

  @Test
  @DisplayName("Gruppen werden ausgegeben")
  void test_1() {
    String personA = "personA";
    GruppenService gruppenService = new GruppenService();

    gruppenService.addGruppe(personA, "gruppe1");
    Set<Gruppe> gruppeForPersonA = gruppenService.getGruppenForGithubName("personA");

    assertThat(gruppeForPersonA).extracting(
        Gruppe::getName
    ).containsExactly("gruppe1");
  }

  @Test
  @DisplayName("Gruppen werden nicht ausgegeben, wenn nicht Mitglied")
  void test_2() {
    String personA = "personA";

    GruppenService gruppenService = new GruppenService();

    gruppenService.addGruppe(personA, "gruppe1");
    Set<Gruppe> gruppeForPersonB = gruppenService.getGruppenForGithubName("personB");

    assertThat(gruppeForPersonB).isEmpty();
  }

  @Test
  @DisplayName("Nur eigene Gruppen werden ausgegeben. Und mehrere Personen sind in den Gruppen")
  void test_3() {
    String personA = "personA";
    String personB = "personB";

    GruppenService gruppenService = new GruppenService();

    Long gruppe1Id = gruppenService.addGruppe(personA, "gruppe1");
    gruppenService.addGruppe(personB, "gruppe2");
    gruppenService.addUser(personB, gruppe1Id);
    Set<Gruppe> gruppeForPersonA = gruppenService.getGruppenForGithubName("personA");
    Set<Gruppe> gruppeForPersonB = gruppenService.getGruppenForGithubName("personB");

    assertThat(gruppeForPersonA).extracting(
        Gruppe::getName
    ).containsExactly("gruppe1");
    assertThat(gruppeForPersonB).extracting(
        Gruppe::getName
    ).containsExactlyInAnyOrder("gruppe1", "gruppe2");
  }


}
