package store;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * The base class for all objects that can be stored in the Store.
 * <p>
 * This class also includes the {@code ForeignKey} and {@code ForeignSet}
 * classes, which are used to declare and maintain relations between
 * {@code StoredObject}s. We need these in order to properly maintain lifecycles
 * of {@code StoredObject}s. (If we naively store these relations as references,
 * deleting any object will create a lot of null pointers to check for.)
 * <p>
 * (Our data model only has one-to-many relations where the "one" side is the
 * parent of the "many" side. This means that every {@code ForeignKey} has a
 * corresponding {@code ForeignSet} (and vice versa) and has
 * {@code "ON DELETE CASCADE"} behavior. We don't use/support other relation
 * constructs)
 */
public abstract class StoredObject implements Serializable, Identifiable {
  protected final transient Store store;
  protected final String id;
  /**
   * The list of {@code ForeignKey} objects declared in the subclass
   */
  private ArrayList<ForeignKey<?>> foreignKeys = new ArrayList<>();
  /**
   * The list of {@code ForeignSet} objects declared in the subclass.
   */
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

  /**
   * Deletes this object from the store. This will also delete all child objects
   * in all the ForeignSets declared in this object.
   */
  public void delete() {
    // System.out.println("Deleting " + this.getClass().getSimpleName() + " " + id);

    // Collect all objects to delete; need to avoid deleting an object while
    // iterating
    ArrayList<StoredObject> objectsToDelete = new ArrayList<>();
    for (ForeignSet<?> foreignSet : foreignSets) {
      for (StoredObject obj : foreignSet) {
        if (obj != this) {
          objectsToDelete.add(obj);
        }
      }
    }

    // Delete all child objects. The child will also delete its own children if
    // it has any.
    for (StoredObject obj : objectsToDelete) {
      obj.delete();
    }

    // if needed, we can use foreignKeys here to alert ForeignSets containing this
    // object (probably needed if implementing caching or indices)

    store.delete(this);
  }

  /**
   * Represents a "lazy" reference to {@code StoredObject} of type {@code T}. We
   * store the ID of the referred object and look it up in the store when needed.
   * 
   * It's not enough to just store Java references we would need to remove all
   * references to an object to delete it.
   */
  public class ForeignKey<T extends StoredObject> implements Serializable {
    private final Class<T> type;
    private String id = null;

    public ForeignKey(Class<T> type) {
      this.type = type;

      // Add to list of declared ForeignKeys in the parent object
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

  /**
   * Represents a set of {@code StoredObject}s which refer to this object through
   * a {@code ForeignKey}. (This is a one-to-many relation, where this object is
   * the "one" side.)
   * <p>
   * Requires that the foreign object has a field called {@code "fieldName"}
   * containing a {@code ForeignKey} to this object (uses reflection to find and
   * verify the field).
   * <p>
   * Uses a naive (and inefficient) filter implementation for iterating over the
   * objects. Can implement caching or indices to solve this if it becomes a
   * performance issue.
   */
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

      // Add to list of declared ForeignSets in the parent object
      StoredObject.this.foreignSets.add(this);
    }

    /**
     * Returns the field in the foreign object that refers to this object.
     * <p>
     * Verifies that it is of type {@code ForeignKey<This>}, where {@code This} is
     * the type of the referent (the object declaring the {@code ForeignSet}).
     */
    private Field getField() {
      // We only need to check the field once, so we can cache it.
      if (field != null) {
        return field;
      }

      Field field;
      // Get the field `fieldName` in the foreign object
      try {
        field = type.getDeclaredField(fieldName);
      } catch (NoSuchFieldException e) {
        e.printStackTrace();
        throw new RuntimeException("Field not found", e);
      }

      RuntimeException wrongFieldException = new IllegalArgumentException(
          type.getSimpleName() + "." + fieldName + " must be of type ForeignKey<"
              + referent.getClass().getSimpleName() + ">");

      // Check that the field is a ForeignKey
      if (!field.getType().equals(ForeignKey.class)) {
        throw wrongFieldException;
      }

      // Check that the field is a parameterized type with one argument
      Type genericType = field.getGenericType();
      if (!(genericType instanceof ParameterizedType)) {
        throw wrongFieldException;
      }
      Type[] typeArgs = ((ParameterizedType) genericType).getActualTypeArguments();
      if (typeArgs.length != 1 || !(typeArgs[0] instanceof Class<?>)) {
        throw wrongFieldException;
      }

      // Check that the field is a ForeignKey<This>
      Class<?> fieldType = (Class<?>) typeArgs[0];
      if (!referent.getClass().isAssignableFrom(fieldType)) {
        throw wrongFieldException;
      }

      // Make the field accessible
      field.setAccessible(true);

      // Cache the field
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
      // Filters the objects of type T in the store by checking if their
      // ForeignKey field points to this object.
      return new Iterator<T>() {
        // Iterator over all objects of type T in the store
        private final Iterator<T> iterator = store.getAll(type).iterator();
        private T next;

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
          if (next == null) {
            next = findNext();
          }
          return next != null;
        }

        @Override
        public T next() {
          if (!hasNext()) {
            throw new NoSuchElementException("No more elements");
          }
          T result = next;
          next = findNext();
          return result;
        }
      };
    }
  }

}
