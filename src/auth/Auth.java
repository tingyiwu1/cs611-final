package auth;

import java.util.Optional;

import obj.User;
import store.Store;

public class Auth {
  private final Store store;

  private Optional<User> user;

  public Auth(Store store) {
    this.store = store;
    this.user = Optional.empty();
  }

  public boolean isLoggedIn() {
    return user.isPresent();
  }

  public Optional<User> login(String id) {
    if (isLoggedIn()) {
      throw new IllegalStateException("Already logged in");
    }
    user = store.get(User.class, id);
    return user;
  }

  public void logout() {
    user = Optional.empty();
  }

  public User getUser() {
    if (!isLoggedIn()) {
      throw new IllegalStateException("Not logged in");
    }
    return user.get();
  }

}
