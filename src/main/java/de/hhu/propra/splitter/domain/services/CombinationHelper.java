package de.hhu.propra.splitter.domain.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class CombinationHelper {

  public static <T> Set<Set<T>> allCombinationsOf(List<T> items) {
    return addAndNot(
        new HashSet<Set<T>>(
            Set.of(new HashSet<>())
        ),
        items
    );
  }

  private static <T> Set<Set<T>> addAndNot(
      Set<Set<T>> combinations,
      List<T> items
  ) {
    int i = 0;
    while (i != items.size()) {
      var combinationsWithNewNumber = copySet(combinations);
      int finalI = i;
      combinationsWithNewNumber.forEach(combination -> combination.add(items.get(finalI)));
      combinations.addAll(combinationsWithNewNumber);
      i++;
    }

    return combinations;
  }

  private static <T> Set<Set<T>> copySet(Set<Set<T>> orig) {
    var result = new HashSet<Set<T>>();
    for (var item : orig) {
      result.add(new HashSet<>(Set.copyOf(item)));
    }
    return result;
  }
}
