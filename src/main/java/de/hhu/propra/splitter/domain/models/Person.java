package de.hhu.propra.splitter.domain.models;

import de.hhu.propra.splitter.stereotypes.Entity;

@Entity
public class Person {

  private String gitHubName;

  public String getGitHubName() {
    return gitHubName;
  }

  Person(String gitHubName) {
    this.gitHubName = gitHubName;
  }
}
