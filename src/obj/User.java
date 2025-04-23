package obj;

import store.Store;
import store.StoredObject;

public abstract class User extends StoredObject {

  private final String userId;
  private String name;

  public User(Store store, String id, String name) {
    super(store);
    this.userId = id;
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getId() {
    return userId;
  }
}
