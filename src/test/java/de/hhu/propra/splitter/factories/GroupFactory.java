package de.hhu.propra.splitter.factories;

import de.hhu.propra.splitter.domain.Debit;
import de.hhu.propra.splitter.domain.Group;
import de.hhu.propra.splitter.domain.User;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class GroupFactory {

  private Set<User> members = new HashSet<>(Set.of(
      new UserFactory().build()
  ));
  private String name = "Group 1";
  private boolean open = true;
  private Set<Debit> debits = new HashSet<>();

  public GroupFactory withName(String name) {
    this.name = name;
    return this;
  }

  public GroupFactory withOpen(boolean open) {
    this.open = open;
    return this;
  }

  public GroupFactory withMembers(Set<User> members) {
    assert members.size() > 0;
    this.members = members;
    return this;
  }

  public GroupFactory withDebits(Set<Debit> debits) {
    this.debits = debits;
    return this;
  }

  public Group build() {
    Iterator<User> userIterator = this.members.iterator();
    Group object = new Group(userIterator.next(), this.name);
    while (userIterator.hasNext()) {
      object.addMember(userIterator.next());
    }
    for (Debit debit : this.debits) {
      object.addDebit(debit);
    }
    return object;
  }
}
