package auth;

import obj.User;

public interface IAuth {
  boolean isLoggedIn();

  // I think we don't need username/password, just login with a single string
  // identifier
  void login(String username, String password);

  void logout();

  User getUser();
}
