package de.hhu.propra.splitter.domain;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import de.hhu.propra.splitter.factories.AusgabeFactory;
import de.hhu.propra.splitter.factories.GruppeFactory;
import de.hhu.propra.splitter.factories.PersonFactory;
import java.util.Set;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;

public class GruppeTest {

  @Test
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
    ).containsExactly(
        tuple(personB, personA, Money.of(20, "EUR"))
    );
  }
}
