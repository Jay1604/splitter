package de.hhu.propra.splitter.domain;

import java.util.HashSet;
import java.util.Set;
import org.javamoney.moneta.Money;

public class Debit {
  private String description;
  private Money amount;
  private User creditor;
  private Set<User> debtor = new HashSet<>();

  public Debit(String description, Money amount, User creditor, Set<User> debtor) {
    this.description = description;
    this.amount = Money.from(amount);
    this.creditor = creditor;
    this.debtor.addAll(debtor);
  }

  public String getDescription() {
    return description;
  }

  public Money getAmount() {
    return Money.from(amount);
  }

  public User getCreditor() {
    return creditor;
  }

  public Set<User> getDebtor() {
    return Set.copyOf(debtor);
  }
}
