package de.hhu.propra.splitter.services;

import static org.assertj.core.api.Assertions.assertThat;

import de.hhu.propra.splitter.domain.models.Gruppe;
import de.hhu.propra.splitter.persistence.GruppenRepositoryImpl;
import de.hhu.propra.splitter.persistence.SpringDataGruppenRepository;
import de.hhu.propra.splitter.persistence.SpringDataPersonenRepository;
import java.util.Set;
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
  void init (){
    gruppenRepository = new GruppenRepositoryImpl(springPersonenRepository,springGruppenRepository);
  }

  @Test
  @DisplayName("Gruppen werden ausgegeben")
  @Sql("clear__tables.sql")
  void test_1() {
    String personA = "personA";
    GruppenService gruppenService = new GruppenService(gruppenRepository);

    gruppenService.addGruppe(personA, "gruppe1");
    Set<Gruppe> gruppeForPersonA = gruppenService.getGruppenForGithubName("personA");

    assertThat(gruppeForPersonA).extracting(
        Gruppe::getName
    ).containsExactly("gruppe1");
  }

  @Test
  @DisplayName("Gruppen werden nicht ausgegeben, wenn nicht Mitglied")
  @Sql("clear__tables.sql")
  void test_2() {
    String personA = "personA";

    GruppenService gruppenService = new GruppenService(gruppenRepository);

    gruppenService.addGruppe(personA, "gruppe1");
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

    Long gruppe1Id = gruppenService.addGruppe(personA, "gruppe1");
    gruppenService.addGruppe(personB, "gruppe2");
    gruppenService.addPersonToGruppe(personB, gruppe1Id);
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
