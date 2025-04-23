package auth;

import obj.User;

public interface IAuth {
  boolean isLoggedIn();

  // I think we don't need username/password, just login with a single string
  // identifier
  void login(String id);

  void logout();

  User getUser();
}
