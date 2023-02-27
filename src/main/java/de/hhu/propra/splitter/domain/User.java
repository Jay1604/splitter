package de.hhu.propra.splitter.domain;

public class User {
  private String gitHubHandle;

  public String getGitHubHandle() {
    return gitHubHandle;
  }

  public User(String gitHubHandle) {
    this.gitHubHandle = gitHubHandle;
  }
}
