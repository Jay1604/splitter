package de.hhu.propra.splitter.domain;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import de.hhu.propra.splitter.domain.model.Gruppe;
import de.hhu.propra.splitter.domain.model.Person;
import de.hhu.propra.splitter.domain.model.Ueberweisung;
import de.hhu.propra.splitter.domain.service.AusgleichService;
import de.hhu.propra.splitter.factories.AusgabeFactory;
import de.hhu.propra.splitter.factories.GruppeFactory;
import java.util.Set;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GruppeTest {

  @Autowired
  AusgleichService ausgleichService;

  @Test
  @DisplayName("A legt B 20€ aus")
  public void test_01() {
    String personA = "nutzer1";
    String personB = "nutzer2";

    Gruppe gruppe = new GruppeFactory().withMitglieder(Set.of(personA, personB))
        .withAusgaben(Set.of(
            new AusgabeFactory().withGlaeubiger(personA).withSchuldner(Set.of(
                personB, personA
            )).withBetrag(Money.of(40, "EUR")).build()
        )).build();

    Set<Ueberweisung> ausgleich = ausgleichService.ausgleichen(gruppe);

    assertThat(ausgleich).extracting(
        (a -> a.getSender().getGitHubName()),
        (a -> a.getEmpfaenger().getGitHubName()),
        Ueberweisung::getBetrag
    ).containsExactlyInAnyOrder(
        tuple(personB, personA, Money.of(20, "EUR"))
    );
  }

  @Test
  @DisplayName("Keine Transaktionen")
  void test_02() {
    Gruppe gruppe = new GruppeFactory().build();

    Set<Ueberweisung> ausgleich = ausgleichService.ausgleichen(gruppe);
    assertThat(ausgleich).isEmpty();
  }

  @Test
  @DisplayName("2 Personen legen jeweils 2 Personen 20€ aus")
  public void test_03() {
    String personA = "nutzer1";
    String personB = "nutzer2";
    String personC = "nutzer3";
    String personD = "nutzer4";

    Gruppe gruppe = new GruppeFactory().withMitglieder(Set.of(personA, personB, personC, personD))
        .withAusgaben(Set.of(
            new AusgabeFactory().withGlaeubiger(personA).withSchuldner(Set.of(
                personB, personA
            )).withBetrag(Money.of(40, "EUR")).build(),
            new AusgabeFactory().withGlaeubiger(personC).withSchuldner(Set.of(
                personD, personC
            )).withBetrag(Money.of(40, "EUR")).build()
        )).build();

    Set<Ueberweisung> ausgleich = ausgleichService.ausgleichen(gruppe);
    assertThat(ausgleich).extracting(
        (a -> a.getSender().getGitHubName()),
        Ueberweisung::getBetrag
    ).containsOnly(
        tuple(personB, Money.of(20, "EUR")),
        tuple(personD, Money.of(20, "EUR"))
    );
    assertThat(ausgleich).extracting(
        (a -> a.getEmpfaenger().getGitHubName()),
        Ueberweisung::getBetrag
    ).containsExactlyInAnyOrder(
        tuple(personA, Money.of(20, "EUR")),
        tuple(personC, Money.of(20, "EUR"))
    );

  }

  @Test
  @DisplayName("A legt B und C 10€ aus")
  public void test_04() {
    String personA = "nutzer1";
    String personB = "nutzer2";
    String personC = "nutzer3";

    Gruppe gruppe = new GruppeFactory().withMitglieder(Set.of(personA, personB, personC))
        .withAusgaben(Set.of(
            new AusgabeFactory().withGlaeubiger(personA).withSchuldner(Set.of(
                personB, personC, personA
            )).withBetrag(Money.of(30, "EUR")).build()
        )).build();

    Set<Ueberweisung> ausgleich = ausgleichService.ausgleichen(gruppe);

    assertThat(ausgleich).extracting(
        (a -> a.getSender().getGitHubName()),
        (a -> a.getEmpfaenger().getGitHubName()),
        Ueberweisung::getBetrag
    ).containsExactlyInAnyOrder(
        tuple(personB, personA, Money.of(10, "EUR")),
        tuple(personC, personA, Money.of(10, "EUR"))
    );
  }

  @Test
  @DisplayName("A legt B und C 10€ aus während B C 50€ auslegt ")
  public void test_05() {
    String personA = "nutzer1";
    String personB = "nutzer2";
    String personC = "nutzer3";

    Gruppe gruppe = new GruppeFactory().withMitglieder(Set.of(personA, personB, personC))
        .withAusgaben(Set.of(
            new AusgabeFactory().withGlaeubiger(personA).withSchuldner(Set.of(
                personB, personC, personA
            )).withBetrag(Money.of(30, "EUR")).build(),
            new AusgabeFactory().withGlaeubiger(personB).withSchuldner(Set.of(
                personC, personB
            )).withBetrag(Money.of(100, "EUR")).build()
        )).build();

    Set<Ueberweisung> ausgleich = ausgleichService.ausgleichen(gruppe);

    assertThat(ausgleich).extracting(
        (a -> a.getSender().getGitHubName()),
        (a -> a.getEmpfaenger().getGitHubName()),
        Ueberweisung::getBetrag
    ).containsExactlyInAnyOrder(
        tuple(personC, personA, Money.of(20, "EUR")),
        tuple(personC, personB, Money.of(40, "EUR"))
    );
  }

  @Test
  @DisplayName("Beispielaufgabe: Urlaub")
  public void test_06() {
    String personA = "nutzer1";
    String personB = "nutzer2";
    String personC = "nutzer3";
    String personD = "nutzer4";
    String personE = "nutzer5";
    String personF = "nutzer6";

    Gruppe gruppe = new GruppeFactory().withMitglieder(
        Set.of(personA, personB, personC, personD, personE, personF)).withAusgaben(Set.of(
          new AusgabeFactory().withGlaeubiger(personA).withSchuldner(Set.of(
            personB, personC, personD, personE, personF, personA
        )).withBetrag(Money.of(564, "EUR")).build(),
          new AusgabeFactory().withGlaeubiger(personB).withSchuldner(Set.of(
            personA, personB
        )).withBetrag(Money.of(77.16, "EUR").divide(2)).build(),
          new AusgabeFactory().withGlaeubiger(personB).withSchuldner(Set.of(
            personA, personD, personB
        )).withBetrag(Money.of(77.16, "EUR").divide(2)).build(),
          new AusgabeFactory().withGlaeubiger(personC).withSchuldner(Set.of(
            personE, personF, personC
        )).withBetrag(Money.of(82.11, "EUR")).build(),
          new AusgabeFactory().withGlaeubiger(personD).withSchuldner(Set.of(
            personB, personC, personA, personE, personF, personD
        )).withBetrag(Money.of(96, "EUR")).build(),
          new AusgabeFactory().withGlaeubiger(personF).withSchuldner(Set.of(
            personB, personE, personF
        )).withBetrag(Money.of(95.37, "EUR")).build()

    )).build();

    Set<Ueberweisung> ausgleich = ausgleichService.ausgleichen(gruppe);

    assertThat(ausgleich).extracting(
        (a -> a.getSender().getGitHubName()),
        (a -> a.getEmpfaenger().getGitHubName()),
        Ueberweisung::getBetrag
    ).containsExactlyInAnyOrder(
        tuple(personB, personA, Money.of(96.78, "EUR")),
        tuple(personC, personA, Money.of(55.26, "EUR")),
        tuple(personD, personA, Money.of(26.86, "EUR")),
        tuple(personE, personA, Money.of(169.16, "EUR")),
        tuple(personF, personA, Money.of(73.79, "EUR"))
    );
  }

  @Test
  @DisplayName("Zirkeltest")
  public void test_07() {
    String personA = "nutzer1";
    String personB = "nutzer2";
    String personC = "nutzer3";

    Gruppe gruppe = new GruppeFactory().withMitglieder(Set.of(personA, personB, personC))
        .withAusgaben(Set.of(
            new AusgabeFactory().withGlaeubiger(personA).withSchuldner(Set.of(
                personB, personA
            )).withBetrag(Money.of(40, "EUR")).build(),
            new AusgabeFactory().withGlaeubiger(personB).withSchuldner(Set.of(
                personC, personB
            )).withBetrag(Money.of(40, "EUR")).build(),
            new AusgabeFactory().withGlaeubiger(personC).withSchuldner(Set.of(
                personA, personC
            )).withBetrag(Money.of(40, "EUR")).build()
        )).build();

    Set<Ueberweisung> ausgleich = ausgleichService.ausgleichen(gruppe);

    assertThat(ausgleich).isEmpty();
  }


}
