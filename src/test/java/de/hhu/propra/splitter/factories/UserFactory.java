package de.hhu.propra.splitter.factories;

import de.hhu.propra.splitter.domain.User;

public class UserFactory {

  private String gitHubHandle = "GitHubUser1";

  public UserFactory withGitHubHandle(String gitHubHandle) {
    this.gitHubHandle = gitHubHandle;
    return this;
  }

  public User build() {
    return new User(this.gitHubHandle);
  }
}
