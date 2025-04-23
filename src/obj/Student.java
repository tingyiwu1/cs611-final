package obj;

import store.StoredObject;
import store.Store;

public class Student extends StoredObject {
  public final String id;
  public final String name;

  public Student(Store store, String id, String name) {
    super(store);
    this.id = id;
    this.name = name;
  }

  @Override
  public String getId() {
    return id;
  }
}
