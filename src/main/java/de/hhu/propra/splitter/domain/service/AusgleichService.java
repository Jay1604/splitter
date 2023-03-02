package de.hhu.propra.splitter.domain.service;

import de.hhu.propra.splitter.domain.model.Ausgabe;
import de.hhu.propra.splitter.domain.model.Gruppe;
import de.hhu.propra.splitter.domain.model.Person;
import de.hhu.propra.splitter.domain.model.Ueberweisung;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;

@Service
public class AusgleichService {

  private HashMap<Person, Money> schuldenBerechnen(Gruppe gruppe) {
    HashMap<Person, Money> debts = new HashMap<>();
    for (Ausgabe ausgabe : gruppe.getAusgaben()) {
      Money beitrag = ausgabe.getBetrag().divide(ausgabe.getSchuldner().size());
      debts.put(ausgabe.getGlaeubiger(),
          debts.getOrDefault(
              ausgabe.getGlaeubiger(),
              Money.of(0, "EUR")
          ).add(ausgabe.getBetrag())
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

  public Set<Ueberweisung> ausgleichen(Gruppe gruppe) {
    Set<Ueberweisung> ueberweisungen = new HashSet<>();

    HashMap<Person, Money> debts = schuldenBerechnen(gruppe);
    while (debts.values().stream().filter(value -> !value.isZero()).toList().size() > 0) {
      findPerfectMatch(ueberweisungen, debts);
      findGreedy(ueberweisungen, debts);
    }
    return ueberweisungen;
  }

  private void findPerfectMatch(Set<Ueberweisung> ueberweisungen, HashMap<Person, Money> debts) {
    for (var dept : debts.entrySet()) {
      if (dept.getValue().isZero()) {
        continue;
      }

      // Find perfect combination (subset of debts)
      var combinations = AllCombinations.allCombinations(
          // Only negative Money values if dept is positive and via versa
          debts.entrySet().stream().filter(
              entry -> (entry.getValue().isNegative() && dept.getValue().isPositive())
                  || (entry.getValue().isPositive() && dept.getValue().isNegative())
          ).toList()
      ).stream().filter(
          // Filter for fitting combinations, so 1) add Money values of combinations
          // 2) filter for correct combinations
          combination -> combination.stream().map(Entry::getValue).reduce(
              Money.of(0, "EUR"),
              Money::add
          ).equals(dept.getValue().multiply(-1))
      ).sorted(Comparator.comparingInt(Set::size)).toList();

      if (combinations.size() > 0) {
        Set<Entry<Person, Money>> partnerEntries = combinations.get(0);
        if (dept.getValue().isNegative()) {
          for (Entry<Person, Money> partnerEntry : partnerEntries) {
            ueberweisungen.add(new Ueberweisung(
                dept.getKey(),
                partnerEntry.getKey(),
                partnerEntry.getValue()
            ));
          }
        } else {
          for (Entry<Person, Money> partnerEntry : partnerEntries) {
            ueberweisungen.add(new Ueberweisung(
                partnerEntry.getKey(),
                dept.getKey(),
                partnerEntry.getValue().multiply(-1)
            ));
          }
        }
        debts.put(dept.getKey(), Money.of(0, "EUR"));
        for (Entry<Person, Money> partnerEntry : partnerEntries) {
          debts.put(partnerEntry.getKey(), Money.of(0, "EUR"));
        }
      }

      //      Optional<Entry<Person, Money>> perfectMatch = debts.entrySet().stream().filter(
      //          entry -> entry.getValue().equals(dept.getValue().multiply(-1))
      //      ).findFirst();
      //      if (perfectMatch.isPresent()) {
      //        // Check who needs to send money
      //        if (dept.getValue().isNegative()) {
      //          ueberweisungen.add(new Ueberweisung(
      //              dept.getKey(),
      //              perfectMatch.get().getKey(),
      //              perfectMatch.get().getValue()
      //          ));
      //        } else {
      //          ueberweisungen.add(new Ueberweisung(
      //              perfectMatch.get().getKey(),
      //              dept.getKey(),
      //              dept.getValue()
      //          ));
      //          // After perfect match both have 0 dept
      //
      //        }
      //        debts.put(dept.getKey(), Money.of(0, "EUR"));
      //        debts.put(perfectMatch.get().getKey(), Money.of(0, "EUR"));
      //      }
    }
  }

  private void findGreedy(Set<Ueberweisung> ueberweisungen, HashMap<Person, Money> debts) {
    Optional<Entry<Person, Money>> max = debts.entrySet().stream().max(Entry.comparingByValue());
    Optional<Entry<Person, Money>> min = debts.entrySet().stream().min(Entry.comparingByValue());
    if (min.isPresent() && max.isPresent() && !max.get().getValue().isZero()) {
      Money betrag = max.get().getValue();
      if (min.get().getValue().abs().isLessThan(betrag)) {
        betrag = min.get().getValue().abs();
      }
      ueberweisungen.add(new Ueberweisung(min.get().getKey(), max.get().getKey(), betrag));
      max.get().setValue(max.get().getValue().subtract(betrag));
      min.get().setValue(min.get().getValue().add(betrag));
    }
  }
}
