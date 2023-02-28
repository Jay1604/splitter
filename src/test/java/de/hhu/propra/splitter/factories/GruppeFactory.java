package de.hhu.propra.splitter.factories;

import de.hhu.propra.splitter.domain.Ausgabe;
import de.hhu.propra.splitter.domain.Gruppe;
import de.hhu.propra.splitter.domain.Person;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@SuppressFBWarnings("EI_EXPOSE_REP2")
public class GruppeFactory {

  private Set<Person> mitglieder = new HashSet<>(Set.of(
      new PersonFactory().build()
  ));
  private String name = "Group 1";
  private boolean istOffen = true;
  private Set<Ausgabe> ausgaben = new HashSet<>();

  public GruppeFactory withName(String name) {
    this.name = name;
    return this;
  }

  public GruppeFactory withIstOffen(boolean istOffen) {
    this.istOffen = istOffen;
    return this;
  }

  public GruppeFactory withMitglieder(Set<Person> mitglieder) {
    assert mitglieder.size() > 0;
    this.mitglieder = mitglieder;
    return this;
  }

  public GruppeFactory withAusgaben(Set<Ausgabe> ausgaben) {
    this.ausgaben = ausgaben;
    return this;
  }

  public Gruppe build() {
    Iterator<Person> userIterator = this.mitglieder.iterator();
    Gruppe object = new Gruppe(userIterator.next(), this.name);
    while (userIterator.hasNext()) {
      object.addMitglied(userIterator.next());
    }
    for (Ausgabe debit : this.ausgaben) {
      object.addAusgabe(debit);
    }
    return object;
  }
}
