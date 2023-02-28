package de.hhu.propra.splitter.domain;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import de.hhu.propra.splitter.factories.AusgabeFactory;
import de.hhu.propra.splitter.factories.GruppeFactory;
import de.hhu.propra.splitter.factories.PersonFactory;
import java.util.Set;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class GruppeTest {

  @Test
  @DisplayName("A legt B 20€ aus")
  public void test_01() {
    Person personA = new PersonFactory().withGitHubName("personA").build();
    Person personB = new PersonFactory().withGitHubName("personB").build();
//  Person personC = new PersonFactory().withGitHubName("personC").build();
//  Person personD = new PersonFactory().withGitHubName("personD").build();

    Gruppe gruppe = new GruppeFactory().withAusgaben(Set.of(
            new AusgabeFactory().withGlaeubiger(personA).withSchuldner(Set.of(
                    personB
            )).withBetrag(Money.of(40, "EUR")).build()
    )).build();

    Set<Ueberweisung> ausgleich = gruppe.ausgleichen();

    assertThat(ausgleich).extracting(
            Ueberweisung::getSender,
            Ueberweisung::getEmpfaenger,
            Ueberweisung::getBetrag
    ).containsExactlyInAnyOrder(
            tuple(personB, personA, Money.of(20, "EUR"))
    );
  }

  @Test
  @DisplayName("Keine Transaktionen")
  void test_02() {
    Gruppe gruppe = new GruppeFactory().build();

    Set<Ueberweisung> ausgleich = gruppe.ausgleichen();
    assertThat(ausgleich).isEmpty();
  }

  @Test
  @DisplayName("2 Personen legen jeweils 2 Personen 20€ aus")
  public void test_03() {
    Person personA = new PersonFactory().withGitHubName("personA").build();
    Person personB = new PersonFactory().withGitHubName("personB").build();
    Person personC = new PersonFactory().withGitHubName("personC").build();
    Person personD = new PersonFactory().withGitHubName("personD").build();

    Gruppe gruppe = new GruppeFactory().withAusgaben(Set.of(
            new AusgabeFactory().withGlaeubiger(personA).withSchuldner(Set.of(
                    personB
            )).withBetrag(Money.of(40, "EUR")).build(),
            new AusgabeFactory().withGlaeubiger(personC).withSchuldner(Set.of(
                    personD
            )).withBetrag(Money.of(40, "EUR")).build()
    )).build();

    Set<Ueberweisung> ausgleich = gruppe.ausgleichen();
    assertThat(ausgleich).extracting(
            Ueberweisung::getSender,
            Ueberweisung::getBetrag
    ).containsOnly(
            tuple(personB, Money.of(20, "EUR")),
            tuple(personD, Money.of(20, "EUR"))
    );
    assertThat(ausgleich).extracting(
            Ueberweisung::getEmpfaenger,
            Ueberweisung::getBetrag
    ).containsExactlyInAnyOrder(
            tuple(personA, Money.of(20, "EUR")),
            tuple(personC, Money.of(20, "EUR"))
    );

  }
  @Test
  @DisplayName("A legt B und C 10€ aus")
  public void test_04() {
    Person personA = new PersonFactory().withGitHubName("personA").build();
    Person personB = new PersonFactory().withGitHubName("personB").build();
    Person personC = new PersonFactory().withGitHubName("personC").build();

    Gruppe gruppe = new GruppeFactory().withAusgaben(Set.of(
            new AusgabeFactory().withGlaeubiger(personA).withSchuldner(Set.of(
                    personB,personC
            )).withBetrag(Money.of(30, "EUR")).build()
    )).build();

    Set<Ueberweisung> ausgleich = gruppe.ausgleichen();

    assertThat(ausgleich).extracting(
            Ueberweisung::getSender,
            Ueberweisung::getEmpfaenger,
            Ueberweisung::getBetrag
    ).containsExactlyInAnyOrder(
            tuple(personB, personA, Money.of(10, "EUR")),
            tuple(personC, personA, Money.of(10, "EUR"))
    );
  }
}
