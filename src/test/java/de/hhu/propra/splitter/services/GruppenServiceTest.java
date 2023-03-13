package de.hhu.propra.splitter.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.hhu.propra.splitter.domain.models.Ausgabe;
import de.hhu.propra.splitter.domain.models.Gruppe;
import de.hhu.propra.splitter.domain.models.Person;
import de.hhu.propra.splitter.exceptions.GruppeNotFoundException;
import de.hhu.propra.splitter.persistence.GruppenRepositoryImpl;
import de.hhu.propra.splitter.persistence.SpringDataGruppenRepository;
import de.hhu.propra.splitter.persistence.SpringDataPersonenRepository;
import java.util.Set;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.test.context.jdbc.Sql;

@DataJdbcTest
public class GruppenServiceTest {

  @Autowired
  SpringDataPersonenRepository springPersonenRepository;
  @Autowired
  SpringDataGruppenRepository springGruppenRepository;

  GruppenRepository gruppenRepository;

  @BeforeEach
  void init() {
    gruppenRepository = new GruppenRepositoryImpl(
        springPersonenRepository,
        springGruppenRepository
    );
  }

  @Test
  @DisplayName("Gruppen werden ausgegeben")
  @Sql("clear__tables.sql")
  void test_1() {
    String personA = "personA";
    GruppenService gruppenService = new GruppenService(gruppenRepository);

    gruppenService.addGruppe(
        personA,
        "gruppe1"
    );
    Set<Gruppe> gruppeForPersonA = gruppenService.getGruppenForGithubName("personA");

    assertThat(gruppeForPersonA)
        .extracting(
            Gruppe::getName
        )
        .containsExactly("gruppe1");
  }

  @Test
  @DisplayName("Gruppen werden nicht ausgegeben, wenn nicht Mitglied")
  @Sql("clear__tables.sql")
  void test_2() {
    String personA = "personA";

    GruppenService gruppenService = new GruppenService(gruppenRepository);

    gruppenService.addGruppe(
        personA,
        "gruppe1"
    );
    Set<Gruppe> gruppeForPersonB = gruppenService.getGruppenForGithubName("personB");

    assertThat(gruppeForPersonB).isEmpty();
  }

  @Test
  @DisplayName("Nur eigene Gruppen werden ausgegeben. Und mehrere Personen sind in den Gruppen")
  @Sql("clear__tables.sql")
  void test_3() {
    String personA = "personA";
    String personB = "personB";

    GruppenService gruppenService = new GruppenService(gruppenRepository);

    Long gruppe1Id = gruppenService.addGruppe(
        personA,
        "gruppe1"
    );
    gruppenService.addGruppe(
        personB,
        "gruppe2"
    );
    gruppenService.addPersonToGruppe(
        personB,
        gruppe1Id
    );
    Set<Gruppe> gruppeForPersonA = gruppenService.getGruppenForGithubName("personA");
    Set<Gruppe> gruppeForPersonB = gruppenService.getGruppenForGithubName("personB");

    assertThat(gruppeForPersonA)
        .extracting(
            Gruppe::getName
        )
        .containsExactly("gruppe1");
    assertThat(gruppeForPersonB)
        .extracting(
            Gruppe::getName
        )
        .containsExactlyInAnyOrder(
            "gruppe1",
            "gruppe2"
        );
  }

  @Test
  @DisplayName("Es wird die Gruppe nach Id ausgegeben, wenn mitglied")
  @Sql("clear__tables.sql")
  void test_4() {
    String personA = "personA";

    GruppenService gruppenService = new GruppenService(gruppenRepository);

    Long gruppe1Id = gruppenService.addGruppe(
        personA,
        "gruppe1"
    );
    Gruppe gruppeForPersonA = gruppenService.getGruppeForGithubNameById(
        personA,
        gruppe1Id
    );

    assertThat(gruppeForPersonA.getName()).isEqualTo("gruppe1");
  }

  @Test
  @DisplayName("Es wird die Gruppe nach Id nicht ausgegeben, wenn nicht mitglied")
  @Sql("clear__tables.sql")
  void test_5() {
    String personA = "personA";

    GruppenService gruppenService = new GruppenService(gruppenRepository);

    Long gruppe1Id = gruppenService.addGruppe(
        personA,
        "gruppe1"
    );
    assertThrows(
        GruppeNotFoundException.class,
        () -> gruppenService.getGruppeForGithubNameById(
            "NichtPersonA",
            gruppe1Id
        )
    );
  }

  @Test
  @DisplayName("Erstellte Gruppe ist offen")
  @Sql("clear__tables.sql")
  void test_6() {
    String personA = "personA";

    GruppenService gruppenService = new GruppenService(gruppenRepository);

    Long gruppe1Id = gruppenService.addGruppe(
        personA,
        "gruppe1"
    );

    Gruppe gruppe = gruppenService.getGruppeById(gruppe1Id);
    assertThat(gruppe.isOffen()).isTrue();
  }

  @Test
  @DisplayName("Gruppe wird beim aufruf von schliesseGruppe geschlossen")
  @Sql("clear__tables.sql")
  void test_7() {
    String personA = "personA";

    GruppenService gruppenService = new GruppenService(gruppenRepository);

    Long gruppe1Id = gruppenService.addGruppe(
        personA,
        "gruppe1"
    );
    gruppenService.schliesseGruppe(gruppe1Id);

    Gruppe gruppe = gruppenService.getGruppeById(gruppe1Id);
    assertThat(gruppe.isOffen()).isFalse();
  }

  @Test
  @DisplayName("Gruppe nach id gibt korrekte Gruppe zurück")
  @Sql("clear__tables.sql")
  void test_8() {
    String personA = "personA";

    GruppenService gruppenService = new GruppenService(gruppenRepository);

    Long gruppe1Id = gruppenService.addGruppe(
        personA,
        "gruppe1"
    );
    Long gruppe2Id = gruppenService.addGruppe(
        personA,
        "gruppe2"
    );

    Gruppe gruppe = gruppenService.getGruppeById(gruppe2Id);
    assertThat(gruppe.getName()).isEqualTo("gruppe2");
  }

  @Test
  @DisplayName("Gruppe nach id (als String) gibt korrekte Gruppe zurück")
  @Sql("clear__tables.sql")
  void test_9() {
    String personA = "personA";

    GruppenService gruppenService = new GruppenService(gruppenRepository);

    Long gruppe1Id = gruppenService.addGruppe(
        personA,
        "gruppe1"
    );
    Long gruppe2Id = gruppenService.addGruppe(
        personA,
        "gruppe2"
    );

    Gruppe gruppe = gruppenService.getGruppeById(gruppe2Id.toString());
    assertThat(gruppe.getName()).isEqualTo("gruppe2");
  }

  @Test
  @DisplayName("Gruppe nach id raised GruppeNotFoundException wenn keine Gruppe gefunden")
  @Sql("clear__tables.sql")
  void test_10() {
    String personA = "personA";

    GruppenService gruppenService = new GruppenService(gruppenRepository);

    Long gruppe1Id = gruppenService.addGruppe(
        personA,
        "gruppe1"
    );
    assertThrows(
        GruppeNotFoundException.class,
        () -> gruppenService.getGruppeById(gruppe1Id + 1)
    );
  }

  @Test
  @DisplayName("Gruppe nach id (als String) raised GruppeNotFoundException wenn id nicht numerisch")
  @Sql("clear__tables.sql")
  void test_11() {
    String personA = "personA";

    GruppenService gruppenService = new GruppenService(gruppenRepository);

    Long gruppe1Id = gruppenService.addGruppe(
        personA,
        "gruppe1"
    );
    assertThrows(
        GruppeNotFoundException.class,
        () -> gruppenService.getGruppeById("GibtEsNicht")
    );
  }

  @Test
  @DisplayName("Gruppen Service fügt Ausgaben korrekt hinzu")
  @Sql("clear__tables.sql")
  void test_12() {
    String personA = "personA";
    String personB = "personB";

    GruppenService gruppenService = new GruppenService(gruppenRepository);

    Long gruppe1Id = gruppenService.addGruppe(
        personA,
        "gruppe1"
    );
    gruppenService.addPersonToGruppe(
        personB,
        gruppe1Id
    );

    gruppenService.addAusgabe(
        gruppe1Id,
        "TestAusgabe1",
        Money.of(
            20,
            "EUR"
        ),
        personA,
        Set.of(
            personA,
            personB
        )
    );

    Gruppe gruppe = gruppenService.getGruppeById(gruppe1Id);

    assertThat(gruppe.getAusgaben())
        .extracting(
            Ausgabe::getBeschreibung,
            Ausgabe::getBetrag,
            (a -> a
                .getGlaeubiger()
                .getGitHubName())
        )
        .containsExactly(
            tuple(
                "TestAusgabe1",
                Money.of(
                    20,
                    "EUR"
                ),
                personA
            )
        );
    assertThat(gruppe
        .getAusgaben()
        .iterator()
        .next()
        .getSchuldner())
        .extracting(Person::getGitHubName)
        .containsExactlyInAnyOrder(
            personA,
            personB
        );
  }

  @Test
  @DisplayName("Gruppe fügt Personen korrekt hinzu")
  @Sql("clear__tables.sql")
  void test_13() {
    String personA = "personA";
    String personB = "personB";

    GruppenService gruppenService = new GruppenService(gruppenRepository);

    Long gruppe1Id = gruppenService.addGruppe(
        personA,
        "gruppe1"
    );
    gruppenService.addPersonToGruppe(
        personB,
        gruppe1Id
    );

    Gruppe gruppe = gruppenService.getGruppeById(gruppe1Id);

    assertThat(gruppe.getMitglieder())
        .extracting(Person::getGitHubName)
        .containsExactlyInAnyOrder(
            personA,
            personB
        );
  }

  // addPersonToGruppe
}
