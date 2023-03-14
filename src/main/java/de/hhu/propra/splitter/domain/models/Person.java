package de.hhu.propra.splitter.domain.models;

import de.hhu.propra.splitter.stereotypes.Entity;

@Entity
public class Person implements Comparable<Person> {

  private final String gitHubName;

  public String getGitHubName() {
    return gitHubName;
  }

  Person(String gitHubName) {
    this.gitHubName = gitHubName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Person person = (Person) o;

    return gitHubName != null ? gitHubName.equals(person.gitHubName) : person.gitHubName == null;
  }

  @Override
  public int hashCode() {
    return gitHubName != null ? gitHubName.hashCode() : 0;
  }

  @Override
  public int compareTo(Person o) {
    return gitHubName.compareTo(o.gitHubName);
  }
}
