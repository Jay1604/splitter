package de.hhu.propra.splitter.services;

import static org.assertj.core.api.Assertions.assertThat;

import de.hhu.propra.splitter.domain.model.Gruppe;
import de.hhu.propra.splitter.domain.model.Person;
import de.hhu.propra.splitter.factories.AusgabeFactory;
import de.hhu.propra.splitter.factories.GruppeFactory;
import de.hhu.propra.splitter.factories.PersonFactory;
import java.util.Set;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class GruppenServiceTest {

  @Test
  @DisplayName("Gruppen werden ausgegeben")
  void test_1() {
    Person personA = new PersonFactory().withGitHubName("personA").build();
    Gruppe gruppe = new GruppeFactory().withMitglieder(Set.of(personA)).build();

    GruppenService gruppenService = new GruppenService();

    gruppenService.addGruppe(gruppe);
    Set<Gruppe> gruppeForPersonA = gruppenService.getGruppenForGithubName("personA");

    assertThat(gruppeForPersonA).containsExactly(gruppe);
  }

  @Test
  @DisplayName("Gruppen werden nicht ausgegeben, wenn nicht Mitglied")
  void test_2() {
    Person personA = new PersonFactory().withGitHubName("personA").build();
    Gruppe gruppe = new GruppeFactory().withMitglieder(Set.of(personA)).build();

    GruppenService gruppenService = new GruppenService();

    gruppenService.addGruppe(gruppe);
    Set<Gruppe> gruppeForPersonB = gruppenService.getGruppenForGithubName("personB");

    assertThat(gruppeForPersonB).isEmpty();
  }

  @Test
  @DisplayName("Nur eigene Gruppen werden ausgegeben. Und mehrere Personen sind in den Gruppen")
  void test_3() {
    Person personA = new PersonFactory().withGitHubName("personA").build();
    Person personB = new PersonFactory().withGitHubName("personB").build();
    Gruppe gruppe1 = new GruppeFactory().withMitglieder(Set.of(personA, personB)).build();
    Gruppe gruppe2 = new GruppeFactory().withMitglieder(Set.of(personB)).build();

    GruppenService gruppenService = new GruppenService();

    gruppenService.addGruppe(gruppe1);
    gruppenService.addGruppe(gruppe2);
    Set<Gruppe> gruppeForPersonA = gruppenService.getGruppenForGithubName("personA");
    Set<Gruppe> gruppeForPersonB = gruppenService.getGruppenForGithubName("personB");

    assertThat(gruppeForPersonA).containsExactly(gruppe1);
    assertThat(gruppeForPersonB).containsExactlyInAnyOrder(gruppe1, gruppe2);
  }
}
