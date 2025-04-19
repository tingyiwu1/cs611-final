package auth;

import java.util.Optional;

import obj.User;

public class Auth implements IAuth {
  private static final Auth instance = new Auth();

  private Optional<User> user;

  private Auth() {
    this.user = Optional.empty();
  }

  @Override
  public boolean isLoggedIn() {
    return user.isPresent();
  }

  @Override
  public void login(String username, String password) {
    // placeholder
    user = Optional.of(new User(1, "John Doe", User.Role.STUDENT));
  }

  @Override
  public void logout() {
    user = Optional.empty();
  }

  @Override
  public User getUser() {
    if (!isLoggedIn()) {
      throw new IllegalStateException("Not logged in");
    }
    return user.get();
  }

}
