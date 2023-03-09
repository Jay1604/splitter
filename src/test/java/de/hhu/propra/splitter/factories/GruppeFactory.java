package de.hhu.propra.splitter.factories;

import de.hhu.propra.splitter.domain.models.Gruppe;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@SuppressFBWarnings("EI_EXPOSE_REP2")
public class GruppeFactory {

  private Set<String> mitglieder = new HashSet<>(Set.of("nutzer1"));
  private String name = "Group 1";
  private boolean istOffen = true;
  private Set<AusgabeTestobjekt> ausgaben = new HashSet<>();
  private long id = 6;

  public GruppeFactory withId(long id) {
    this.id = id;
    return this;
  }

  public GruppeFactory withName(String name) {
    this.name = name;
    return this;
  }

  public GruppeFactory withIstOffen(boolean istOffen) {
    this.istOffen = istOffen;
    return this;
  }

  public GruppeFactory withMitglieder(Set<String> mitglieder) {
    assert mitglieder.size() > 0;
    this.mitglieder = mitglieder;
    return this;
  }

  public GruppeFactory withAusgaben(Set<AusgabeTestobjekt> ausgaben) {
    this.ausgaben = ausgaben;
    return this;
  }

  public Gruppe build() {
    Iterator<String> userIterator = this.mitglieder.iterator();
    Gruppe object = new Gruppe(id, userIterator.next(), this.name);
    while (userIterator.hasNext()) {
      object.addMitglied(userIterator.next());
    }
    for (AusgabeTestobjekt debit : this.ausgaben) {
      object.addAusgabe(debit.beschreibung(), debit.betrag(), debit.glauebiger(),
          debit.schuldner());
    }
    object.setOffen(this.istOffen);
    return object;
  }
}
