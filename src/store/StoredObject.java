package store;

import java.io.Serializable;

public abstract class StoredObject implements Serializable, Identifiable {
  protected final Store store;

  protected StoredObject(Store store) {
    if (store == null) {
      throw new IllegalArgumentException("Store cannot be null");
    }
    this.store = store;
    store.putNew(this);
  }
}
