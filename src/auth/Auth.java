package auth;

import java.util.Optional;

import obj.Instructor;
import obj.User;

public class Auth implements IAuth {
  private static final Auth INSTANCE = new Auth();

  private Optional<User> user;

  private Auth() {
    this.user = Optional.empty();
  }

  @Override
  public boolean isLoggedIn() {
    return user.isPresent();
  }

  @Override
  public void login(String id) {
    // placeholder
    user = Optional.of(new Instructor(id, "CPK"));
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
