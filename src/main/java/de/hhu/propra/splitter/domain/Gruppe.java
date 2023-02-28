package de.hhu.propra.splitter.domain;

import java.util.*;
import java.util.Map.Entry;

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

  private HashMap<Person, Money> schuldenBerechnen() {
    HashMap<Person, Money> debts = new HashMap<>();
    for (Ausgabe ausgabe : ausgaben) {
      Money beitrag = ausgabe.getBetrag().divide(ausgabe.getSchuldner().size() + 1);
      debts.put(ausgabe.getGlaeubiger(),
          debts.getOrDefault(
              ausgabe.getGlaeubiger(),
              Money.of(0, "EUR")
          ).add(ausgabe.getBetrag()).subtract(beitrag)
      );
      for (Person schuldner : ausgabe.getSchuldner()) {
        debts.put(schuldner,
            debts.getOrDefault(
                schuldner,
                Money.of(0, "EUR")
            ).subtract(beitrag)
        );
      }
    }

    return debts;
  }

  public Set<Ueberweisung> ausgleichen() {
    Set<Ueberweisung> ueberweisungen = new HashSet<>();

    HashMap<Person, Money> debts = schuldenBerechnen();

    // Find perfect matches
    for (var dept : debts.entrySet()) {
      if (dept.getValue().isZero()) {
        continue;
      }
      Optional<Entry<Person, Money>> perfectMatch = debts.entrySet().stream().filter(
          entry -> entry.getValue().equals(dept.getValue().multiply(-1))
      ).findFirst();
      if (perfectMatch.isPresent()) {
        // Check who needs to send money
        if (dept.getValue().isNegative()) {
          ueberweisungen.add(new Ueberweisung(
              dept.getKey(),
              perfectMatch.get().getKey(),
              perfectMatch.get().getValue()
          ));
        } else {
          ueberweisungen.add(new Ueberweisung(
              perfectMatch.get().getKey(),
              dept.getKey(),
              dept.getValue()
          ));
          // After perfect match both have 0 dept

        }
        debts.put(dept.getKey(), Money.of(0, "EUR"));
        debts.put(perfectMatch.get().getKey(), Money.of(0, "EUR"));
      }
    }

    return ueberweisungen;
  }
}
