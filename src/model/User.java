package model;

import store.Store;
import store.StoredObject;

/**
 * Base class for all users in the system. The id is used for logging in as well
 * as any input that chooses a user.
 */
public abstract class User extends StoredObject {

  private String name;

  public User(Store store, String id, String name) {
    super(store, id);
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
