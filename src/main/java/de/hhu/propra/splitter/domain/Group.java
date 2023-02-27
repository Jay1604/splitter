package de.hhu.propra.splitter.domain;

import java.util.HashSet;
import java.util.Set;

public class Group {
  private Set<User> members;
  private String name;
  private boolean open;
  private Set<Debit> debits;

  public Group(User creator, String name) {
    this.members = new HashSet<>(Set.of(creator));
    this.name = name;
    this.open = true;
    this.debits = new HashSet<>();
  }

  public Set<User> getMembers() {
    return Set.copyOf(members);
  }

  public String getName() {
    return name;
  }

  public boolean isOpen() {
    return open;
  }

  public Set<Debit> getDebits() {
    return Set.copyOf(debits);
  }

  public void addMember(User member) {
    this.members.add(member);
  }

  public void addDebit(Debit debit) {
    this.debits.add(debit);
  }

  public void setOpen(boolean open) {
    this.open = open;
  }
}
