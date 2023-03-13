package de.hhu.propra.splitter.domain.models;

import de.hhu.propra.splitter.stereotypes.Entity;
import java.util.HashSet;
import java.util.Set;
import org.javamoney.moneta.Money;

@Entity
public class Ausgabe {

  private String beschreibung;
  private Money betrag;
  private Person glaeubiger;
  private Set<Person> schuldner = new HashSet<>();

  Ausgabe(
      String beschreibung,
      Money betrag,
      Person glaeubiger,
      Set<Person> schuldner
  ) {
    this.beschreibung = beschreibung;
    this.betrag = Money.from(betrag);
    this.glaeubiger = glaeubiger;
    this.schuldner.addAll(schuldner);
  }

  public String getBeschreibung() {
    return beschreibung;
  }

  public Money getBetrag() {
    return Money.from(betrag);
  }

  public Person getGlaeubiger() {
    return glaeubiger;
  }

  public Set<Person> getSchuldner() {
    return Set.copyOf(schuldner);
  }
}
