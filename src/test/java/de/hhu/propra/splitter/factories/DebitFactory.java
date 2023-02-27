package de.hhu.propra.splitter.factories;

import de.hhu.propra.splitter.domain.Debit;
import de.hhu.propra.splitter.domain.User;
import java.util.HashSet;
import java.util.Set;
import org.javamoney.moneta.Money;

public class DebitFactory {

  private String description = "Some description";
  private Money amount = Money.of(0, "EUR");
  private User creditor = new UserFactory().withGitHubHandle("User Creditor").build();
  private Set<User> debtor = new HashSet<>(
      Set.of(new UserFactory().withGitHubHandle("User Debtor 1").build()));

  public DebitFactory withDescription(String description) {
    this.description = description;
    return this;
  }

  public DebitFactory withAmount(Money money) {
    this.amount = money;
    return this;
  }

  public DebitFactory withCreditor(User creditor) {
    this.creditor = creditor;
    return this;
  }

  public DebitFactory withDebtor(Set<User> debtor) {
    this.debtor = debtor;
    return this;
  }

  public Debit build() {
    return new Debit(
        this.description,
        this.amount,
        this.creditor,
        this.debtor
    );
  }
}
