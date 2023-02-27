package de.hhu.propra.splitter.factories;

import de.hhu.propra.splitter.domain.Ausgabe;
import de.hhu.propra.splitter.domain.Person;
import java.util.HashSet;
import java.util.Set;
import org.javamoney.moneta.Money;

public class AusgabeFactory {

  private String beschreibung = "Some description";
  private Money betrag = Money.of(0, "EUR");
  private Person glaeubiger = new PersonFactory().withGitHubName("User Creditor").build();
  private Set<Person> schuldner = new HashSet<>(
      Set.of(new PersonFactory().withGitHubName("User Debtor 1").build()));

  public AusgabeFactory withBeschreibung(String beschreibung) {
    this.beschreibung = beschreibung;
    return this;
  }

  public AusgabeFactory withBetrag(Money betrag) {
    this.betrag = betrag;
    return this;
  }

  public AusgabeFactory withGlaeubiger(Person glaeubiger) {
    this.glaeubiger = glaeubiger;
    return this;
  }

  public AusgabeFactory withSchuldner(Set<Person> schuldner) {
    this.schuldner = schuldner;
    return this;
  }

  public Ausgabe build() {
    return new Ausgabe(
        this.beschreibung,
        this.betrag,
        this.glaeubiger,
        this.schuldner
    );
  }
}
