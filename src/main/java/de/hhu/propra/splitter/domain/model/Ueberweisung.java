package de.hhu.propra.splitter.domain.model;

import de.hhu.propra.splitter.domain.model.Person;
import org.javamoney.moneta.Money;

public class Ueberweisung {
  Person empfaenger;
  Person sender;
  Money betrag;

  public Ueberweisung(Person sender, Person empfaenger, Money betrag) {
    this.empfaenger = empfaenger;
    this.sender = sender;
    this.betrag = Money.from(betrag);
  }

  public Person getEmpfaenger() {
    return empfaenger;
  }

  public Person getSender() {
    return sender;
  }

  public Money getBetrag() {
    return Money.from(betrag);
  }
}
