package de.hhu.propra.splitter.domain.models;

public class Person {

  private String gitHubName;

  public String getGitHubName() {
    return gitHubName;
  }

  Person(String gitHubName) {
    this.gitHubName = gitHubName;
  }
}
