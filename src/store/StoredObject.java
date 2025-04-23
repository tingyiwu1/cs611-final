package store;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

public abstract class StoredObject implements Serializable, Identifiable {
  protected final transient Store store;
  protected final String id;
  private ArrayList<ForeignKey<?>> foreignKeys = new ArrayList<>();
  private ArrayList<ForeignSet<?>> foreignSets = new ArrayList<>();

  protected StoredObject(Store store, String id) {
    if (store == null) {
      throw new IllegalArgumentException("Store cannot be null");
    }
    this.store = store;
    this.id = id;
    store.putNew(this);
  }

  @Override
  public final String getId() {
    return id;
  }

  public void delete() {
    // System.out.println("Deleting " + this.getClass().getSimpleName() + " " + id);

    // avoids deleting an object while iterating
    ArrayList<StoredObject> objectsToDelete = new ArrayList<>();
    for (ForeignSet<?> foreignSet : foreignSets) {
      for (StoredObject obj : foreignSet) {
        if (obj != this) {
          objectsToDelete.add(obj);
        }
      }
    }
    for (StoredObject obj : objectsToDelete) {
      obj.delete();
    }

    // if needed, we can use foreignKeys here to alert ForeignSets containing this
    // object (probably needed if implementing caching or indices)

    store.delete(this);
  }

  public class ForeignKey<T extends StoredObject> implements Serializable {
    private final Class<T> type;
    private String id = null;

    public ForeignKey(Class<T> type) {
      this.type = type;

      StoredObject.this.foreignKeys.add(this);
    }

    public String getId() {
      if (id == null) {
        throw new IllegalArgumentException("ID is not set");
      }
      return id;
    }

    public void setId(String id) {
      if (!store.get(type, id).isPresent()) {
        throw new IllegalArgumentException(type.getSimpleName() + " with ID " + id + " does not exist");
      }
      this.id = id;
    }

    public T get() {
      Optional<T> obj = store.get(type, getId());
      assert obj.isPresent() : "Object not found in store";
      return obj.orElse(null);
    }
  }

  public class ForeignSet<T extends StoredObject> implements Iterable<T>, Serializable {
    public final Class<T> type;
    private final String fieldName;
    private final Identifiable referent;
    private transient Field field = null;

    public ForeignSet(Class<T> type, String fieldName, Identifiable referent) {
      this.type = type;
      this.referent = referent;
      this.fieldName = fieldName;
      getField();
      StoredObject.this.foreignSets.add(this);
    }

    private Field getField() {
      if (field != null) {
        return field;
      }
      Field field;
      try {
        field = type.getDeclaredField(fieldName);
      } catch (NoSuchFieldException e) {
        e.printStackTrace();
        throw new RuntimeException("Field not found", e);
      }
      RuntimeException wrongFieldException = new IllegalArgumentException(
          type.getSimpleName() + "." + fieldName + " must be of type ForeignKey<"
              + referent.getClass().getSimpleName() + ">");
      if (!field.getType().equals(ForeignKey.class)) {
        throw wrongFieldException;
      }
      Type genericType = field.getGenericType();
      if (!(genericType instanceof ParameterizedType)) {
        throw wrongFieldException;
      }
      Type[] typeArgs = ((ParameterizedType) genericType).getActualTypeArguments();
      if (typeArgs.length != 1 || !(typeArgs[0] instanceof Class<?>)) {
        throw wrongFieldException;
      }
      Class<?> fieldType = (Class<?>) typeArgs[0];
      if (!referent.getClass().isAssignableFrom(fieldType)) {
        throw wrongFieldException;
      }
      field.setAccessible(true);
      this.field = field;
      return field;
    }

    public int count() {
      int count = 0;
      Iterator<T> iterator = this.iterator();
      while (iterator.hasNext()) {
        iterator.next();
        count++;
      }
      return count;
    }

    @Override
    public Iterator<T> iterator() {
      return new Iterator<T>() {
        private final Iterator<T> iterator = store.getAll(type).iterator();
        private T current;

        private T findNext() {
          while (iterator.hasNext()) {
            T obj = iterator.next();
            try {
              @SuppressWarnings("unchecked") // checked in getField
              ForeignKey<T> foreignKey = (ForeignKey<T>) getField().get(obj);
              if (foreignKey.getId().equals(referent.getId())) {
                return obj;
              }
            } catch (IllegalAccessException e) {
              // Should be impossible since we set field accessible in getField
              e.printStackTrace();
              throw new RuntimeException("Field access error", e);
            }
          }
          return null;
        }

        @Override
        public boolean hasNext() {
          if (current == null) {
            current = findNext();
          }
          return current != null;
        }

        @Override
        public T next() {
          if (!hasNext()) {
            throw new IllegalStateException("No more elements");
          }
          T result = current;
          current = findNext();
          return result;
        }
      };
    }
  }

}
