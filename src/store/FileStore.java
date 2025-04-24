package store;

import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

/**
 * Concrete implementation of the {@code Store} interface that uses a
 * single file to store {@code StoredObjects}. It uses Java serialization to
 * read and write the objects to the file.
 */
public class FileStore implements Store {

  private final Path dataPath;

  /**
   * The data structure that holds the stored objects and is written to the file.
   * For each subclass of {@code StoredObject}, we maintain a repository, which
   * itself is a {@code HashMap} of IDs to {@code StoredObject}s.
   */
  private final HashMap<Class<? extends StoredObject>, HashMap<String, StoredObject>> repositoryMap;

  @SuppressWarnings("unchecked")
  public FileStore(String dataDir, String fileName) {
    // Check if the data directory exists, if not, create it
    try {
      Files.createDirectories(Paths.get(dataDir));
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to create data directory", e);
    }
    this.dataPath = Paths.get(dataDir, fileName);

    // Read the data file
    HashMap<Class<? extends StoredObject>, HashMap<String, StoredObject>> repositoryMap;
    try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(dataPath))) {
      repositoryMap = (HashMap<Class<? extends StoredObject>, HashMap<String, StoredObject>>) ois.readObject();
    } catch (ClassNotFoundException | InvalidClassException e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to read data file", e);
    } catch (NoSuchFileException e) {
      repositoryMap = new HashMap<>();
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("IOException", e);
    }

    // StoredObject.store is transient, so we need to set it manually for all
    // objects in the store
    for (HashMap<String, StoredObject> repository : repositoryMap.values()) {
      for (StoredObject obj : repository.values()) {

        // Traverse up the class hierarchy to find the StoredObject class
        Class<?> clazz = obj.getClass();
        while (clazz != null) {
          if (clazz.equals(StoredObject.class)) {
            break;
          }
          clazz = clazz.getSuperclass();
        }
        if (clazz == null) {
          throw new RuntimeException("Failed to find StoredObject class");
        }

        // Set the store field to this instance
        try {
          Field f = clazz.getDeclaredField("store");
          f.setAccessible(true);
          f.set(obj, this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
          e.printStackTrace();
          throw new RuntimeException("Failed to set store field", e);
        }
      }
    }

    this.repositoryMap = repositoryMap;
  }

  @Override
  public void save() {
    try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(dataPath))) {
      oos.writeObject(repositoryMap);
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to write data file", e);
    }
  }

  @Override
  public <T extends StoredObject> Optional<T> get(Class<T> clazz, String id) {
    HashMap<String, StoredObject> repository = repositoryMap.get(clazz);
    if (repository != null) {
      @SuppressWarnings("unchecked")
      T obj = (T) repository.get(id);
      return Optional.ofNullable(obj);
    }
    return Optional.empty();
  }

  @Override
  public <T extends StoredObject> void putNew(T obj) {
    HashMap<String, StoredObject> repository = repositoryMap.computeIfAbsent(obj.getClass(), k -> new HashMap<>());
    if (repository.containsKey(obj.getId())) {
      throw new IDExistsException(
          obj.getClass().getSimpleName() + " with ID " + obj.getId() + " already exists.");
    }
    repository.put(obj.getId(), obj);
  }

  @Override
  public <T extends StoredObject> void upsert(T obj) {
    HashMap<String, StoredObject> repository = repositoryMap.computeIfAbsent(obj.getClass(), k -> new HashMap<>());
    repository.put(obj.getId(), obj);
  }

  @Override
  public <T extends StoredObject> void delete(T obj) {
    HashMap<String, StoredObject> repository = repositoryMap.get(obj.getClass());
    if (repository != null) {
      repository.remove(obj.getId());
      if (repository.size() == 0) {
        repositoryMap.remove(obj.getClass());
      }
    }
  }

  @Override
  public <T extends StoredObject> void deleteById(Class<T> clazz, String id) {
    HashMap<String, StoredObject> repository = repositoryMap.get(clazz);
    if (repository != null) {
      repository.remove(id);
      if (repository.size() == 0) {
        repositoryMap.remove(clazz);
      }
    }
  }

  @Override
  public <T extends StoredObject> ArrayList<T> getAll(Class<T> clazz) {
    HashMap<String, StoredObject> repository = repositoryMap.get(clazz);
    ArrayList<T> list = new ArrayList<>();
    if (repository != null) {
      for (StoredObject obj : repository.values()) {
        @SuppressWarnings("unchecked")
        T castedObj = (T) obj;
        list.add(castedObj);
      }
    }
    return list;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("FileStore:\n");
    for (Class<? extends StoredObject> clazz : repositoryMap.keySet()) {
      sb.append("\t").append(clazz.getSimpleName()).append(":\n");
      HashMap<String, StoredObject> repository = repositoryMap.get(clazz);
      for (String id : repository.keySet()) {
        sb.append("\t\t").append(id).append("\n");
      }
    }
    return sb.toString();
  }

}
