package de.hhu.propra.splitter.domain.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class AllCombinations {

  public static <T> Set<Set<T>> allCombinations(List<T> items) {
    return addAndNot(new HashSet<Set<T>>(
        Set.of(new HashSet<>())
    ), items);
  }

  private static <T> Set<Set<T>> addAndNot(Set<Set<T>> combinations,
      List<T> items) {
    // TODO: Change to while loop instead of recursion
    if (items.size() == 0) {
      return combinations;
    }
    var combinationsWithNewElement = copySet(combinations);
    combinationsWithNewElement.forEach(combination -> combination.add(items.get(0)));
    var combinationsWithoutNewElement = copySet(combinations);
    Set<Set<T>> result = new HashSet<>();
    result.addAll(combinationsWithNewElement);
    result.addAll(combinationsWithoutNewElement);

    return addAndNot(result, items.stream().skip(1).toList());
  }

  private static <T> Set<Set<T>> copySet(Set<Set<T>> orig) {
    var result = new HashSet<Set<T>>();
    for (var item : orig) {
      result.add(new HashSet<>(Set.copyOf(item)));
    }
    return result;
  }
}
