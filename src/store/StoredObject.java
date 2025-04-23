package store;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;

public abstract class StoredObject implements Serializable, Identifiable {
  protected final transient Store store;
  protected transient boolean isDeleted = false;

  protected StoredObject(Store store) {
    if (store == null) {
      throw new IllegalArgumentException("Store cannot be null");
    }
    this.store = store;
    store.putNew(this);
  }

  public class ForeignKey<T extends StoredObject> {
    private final Class<T> type;
    private String id;

    public ForeignKey(Class<T> type, String id) {
      this.type = type;
      this.id = id;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public T get() {
      return store.get(type, id).orElse(null);
    }
  }

  public class ForeignSet<T extends StoredObject> implements Iterable<T> {
    public final Class<T> type;
    private final Field field;
    private final String id;

    public ForeignSet(Class<T> type, String fieldName, String id) {
      this.type = type;
      this.id = id;
      try {
        field = type.getDeclaredField(fieldName);
      } catch (NoSuchFieldException e) {
        e.printStackTrace();
        throw new RuntimeException("Field not found", e);
      }
      if (!field.getType().equals(ForeignKey.class)) {
        throw new IllegalArgumentException("Field must be of type ForeignKey<T>");
      }
      Type genericType = field.getGenericType();
      if (!(genericType instanceof ParameterizedType)) {
        throw new IllegalArgumentException("Field must be of type ForeignKey<T>");
      }
      Type[] typeArgs = ((ParameterizedType) genericType).getActualTypeArguments();
      if (typeArgs.length != 1 || !(typeArgs[0] instanceof Class<?>)) {
        throw new IllegalArgumentException("Field must be of type ForeignKey<T>");
      }
      Class<?> fieldType = (Class<?>) typeArgs[0];
      if (!type.isAssignableFrom(fieldType)) {
        throw new IllegalArgumentException("Field must be of type ForeignKey<T>");
      }
      field.setAccessible(true);
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
              @SuppressWarnings("unchecked") // checked in constructor
              ForeignKey<T> foreignKey = (ForeignKey<T>) field.get(obj);
              if (foreignKey.id.equals(id)) {
                return obj;
              }
            } catch (IllegalAccessException e) {
              // Should be impossible since we set field accessible in constructor
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
