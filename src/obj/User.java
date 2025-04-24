package obj;

import store.Store;
import store.StoredObject;

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
