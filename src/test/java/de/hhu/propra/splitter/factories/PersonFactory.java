package de.hhu.propra.splitter.factories;

import de.hhu.propra.splitter.domain.Person;

public class PersonFactory {

  private String gitHubName = "GitHubUser1";

  public PersonFactory withGitHubName(String gitHubName) {
    this.gitHubName = gitHubName;
    return this;
  }

  public Person build() {
    return new Person(this.gitHubName);
  }
}
