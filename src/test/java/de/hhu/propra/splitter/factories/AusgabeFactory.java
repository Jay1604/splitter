package de.hhu.propra.splitter.factories;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.HashSet;
import java.util.Set;
import org.javamoney.moneta.Money;

@SuppressFBWarnings("EI_EXPOSE_REP2")
public class AusgabeFactory {

  private String beschreibung = "Some description";
  private Money betrag = Money.of(0, "EUR");
  private String glaeubiger = "nutzer1";
  private Set<String> schuldner = new HashSet<>(
      Set.of("nutzer1", "nutzer2"));

  public AusgabeFactory withBeschreibung(String beschreibung) {
    this.beschreibung = beschreibung;
    return this;
  }

  public AusgabeFactory withBetrag(Money betrag) {
    this.betrag = betrag;
    return this;
  }

  public AusgabeFactory withGlaeubiger(String glaeubiger) {
    this.glaeubiger = glaeubiger;
    return this;
  }

  public AusgabeFactory withSchuldner(Set<String> schuldner) {
    this.schuldner = schuldner;
    return this;
  }

  public AusgabeTestobjekt build() {
    return new AusgabeTestobjekt(
        this.beschreibung,
        this.betrag,
        this.glaeubiger,
        this.schuldner
    );
  }
}
