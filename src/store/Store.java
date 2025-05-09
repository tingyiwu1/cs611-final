package store;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Interface for a store that manages the persistence of {@code StoredObjects}
 */
public interface Store {

  /**
   * Persists the current state of the store.
   */
  public void save();

  /**
   * Queries the store for an object of type {@code T} with the given id.
   * 
   * @param <T>   Type of the object to be retrieved
   * @param clazz Class of the object to be retrieved
   * @param id    Identifier of the object to be retrieved
   * @return Optional containing the object if found, otherwise empty
   */
  public <T extends StoredObject> Optional<T> get(Class<T> clazz, String id);

  public static class IDExistsException extends RuntimeException {
    public IDExistsException(String message) {
      super(message);
    }
  }

  /**
   * Inserts a new object into the store. Throws an exception if an object with
   * the same id already exists.
   * 
   * @param <T> Type of the object to be inserted
   * @param obj Object to be inserted
   */
  public <T extends StoredObject> void putNew(T obj);

  /**
   * Inserts or updates an object in the store.
   * 
   * @param <T> Type of the object to be updated
   * @param obj Object to be updated
   */
  public <T extends StoredObject> void upsert(T obj);

  /**
   * Deletes an object from the store. Unlike {@code StoredObject.delete()}, this
   * does not consider foreign keys or foreign sets, so calling this directly
   * could lead to broken states.
   * 
   * @param <T> Type of the object to be deleted
   * @param obj Object to be deleted
   */
  public <T extends StoredObject> void delete(T obj);

  /**
   * Deletes an object from the store by its id. Unlike
   * {@code StoredObject.delete()}, this does not consider foreign keys or foreign
   * sets, so calling this directly could lead to broken states.
   * 
   * @param <T>   Type of the object to be deleted
   * @param clazz Class of the object to be deleted
   * @param id    Identifier of the object to be deleted
   */
  public <T extends StoredObject> void deleteById(Class<T> clazz, String id);

  /**
   * Retrieves all objects of a given type from the store.
   * 
   * @param <T>   Type of the objects to be retrieved
   * @param clazz Class of the objects to be retrieved
   */
  public <T extends StoredObject> ArrayList<T> getAll(Class<T> clazz);
}
