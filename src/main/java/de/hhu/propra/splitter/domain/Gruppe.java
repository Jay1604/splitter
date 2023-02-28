package de.hhu.propra.splitter.domain;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import org.javamoney.moneta.Money;

public class Gruppe {

  private Set<Person> mitglieder;
  private String name;
  private boolean istOffen;
  private Set<Ausgabe> ausgaben;

  public Gruppe(Person gruender, String name) {
    this.mitglieder = new HashSet<>(Set.of(gruender));
    this.name = name;
    this.istOffen = true;
    this.ausgaben = new HashSet<>();
  }

  public Set<Person> getMitglieder() {
    return Set.copyOf(mitglieder);
  }

  public String getName() {
    return name;
  }

  public boolean isIstOffen() {
    return istOffen;
  }

  public Set<Ausgabe> getAusgaben() {
    return Set.copyOf(ausgaben);
  }

  public void addMitglied(Person mitglied) {
    this.mitglieder.add(mitglied);
  }

  public void addAusgabe(Ausgabe ausgabe) {
    this.ausgaben.add(ausgabe);
  }

  public void setIstOffen(boolean istOffen) {
    this.istOffen = istOffen;
  }
}
