package de.hhu.propra.splitter.domain.services;

import de.hhu.propra.splitter.domain.models.Ausgabe;
import de.hhu.propra.splitter.domain.models.Gruppe;
import de.hhu.propra.splitter.domain.models.Person;
import de.hhu.propra.splitter.domain.models.Ueberweisung;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;

@Service
public class AusgleichService {

  private HashMap<Person, Money> berechneSchulden(Gruppe gruppe) {
    HashMap<Person, Money> schulden = new HashMap<>();
    for (Ausgabe ausgabe : gruppe.getAusgaben()) {
      Money[] shareAndRemainder = ausgabe.getBetrag()
          .divideAndRemainder(ausgabe.getSchuldner().size());
      Money share = shareAndRemainder[0];
      Money remainder = shareAndRemainder[1];
      schulden.put(ausgabe.getGlaeubiger(),
          schulden.getOrDefault(
              ausgabe.getGlaeubiger(),
              Money.of(0, "EUR")
          ).add(ausgabe.getBetrag())
      );
      for (Person schuldner : ausgabe.getSchuldner()) {
        schulden.put(schuldner,
            schulden.getOrDefault(
                schuldner,
                Money.of(0, "EUR")
            ).subtract(share)
        );
      }
      while (!remainder.isZero()) {
        for (Person schuldner : ausgabe.getSchuldner()) {
          if (remainder.isZero()) {
            break;
          }
          remainder = remainder.subtract(Money.of(0.01, "EUR"));
          schulden.put(schuldner,
              schulden.getOrDefault(
                  schuldner,
                  Money.of(0, "EUR")
              ).subtract(Money.of(0.01, "EUR"))
          );
        }
      }
    }

    return schulden;
  }

  public Set<Ueberweisung> berechneAusgleichUeberweisungen(Gruppe gruppe) {
    Set<Ueberweisung> ueberweisungen = new HashSet<>();
    HashMap<Person, Money> schulden = berechneSchulden(gruppe);
    while (schulden.values().stream().filter(schuld -> !schuld.isZero()).toList().size() > 0) {
      fillPerfectMatch(ueberweisungen, schulden);
      fillGreedy(ueberweisungen, schulden);
    }
    return ueberweisungen;
  }

  private void fillPerfectMatch(Set<Ueberweisung> ueberweisungen, HashMap<Person, Money> debts) {
    for (var dept : debts.entrySet()) {
      if (dept.getValue().isZero()) {
        continue;
      }

      // Find perfect combination (subset of debts)
      var combinations = CombinationHelper.allCombinationsOf(
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
    }
  }

  private void fillGreedy(Set<Ueberweisung> ueberweisungen, HashMap<Person, Money> debts) {
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
