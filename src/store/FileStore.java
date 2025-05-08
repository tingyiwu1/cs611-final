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
    // ensure data directory exists
    try {
      Files.createDirectories(Paths.get(dataDir));
    } catch (IOException e) {
      throw new RuntimeException("Failed to create data directory", e);
    }
    this.dataPath = Paths.get(dataDir, fileName);

    // Try to read the existing file
    HashMap<Class<? extends StoredObject>, HashMap<String, StoredObject>> repo;
    try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(dataPath))) {
      repo = (HashMap<Class<? extends StoredObject>, HashMap<String, StoredObject>>) ois.readObject();

    } catch (InvalidClassException e) {
      // Version mismatch: abandon the old file and start fresh
      System.err
          .println("Warning: serialized store is incompatible (serialVersionUID mismatch), starting with empty store.");
      repo = new HashMap<>();

    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Failed to read data file (class not found)", e);

    } catch (NoSuchFileException e) {
      // File not present yet: first run
      repo = new HashMap<>();

    } catch (IOException e) {
      throw new RuntimeException("IOException while reading data file", e);
    }

    // Rewire the transient `store` field in every deserialized object
    for (HashMap<String, StoredObject> r : repo.values()) {
      for (StoredObject obj : r.values()) {
        // Traverse up the class hierarchy to find the StoredObject class
        Class<?> clazz = obj.getClass();
        while (clazz != null && !clazz.equals(StoredObject.class)) {
          clazz = clazz.getSuperclass();
        }
        if (clazz == null) {
          throw new RuntimeException("StoredObject class not found in hierarchy");
        }

        // Set the store field to this instance
        try {
          Field f = clazz.getDeclaredField("store");
          f.setAccessible(true);
          f.set(obj, this);
        } catch (Exception ex) {
          throw new RuntimeException("Failed to restore store reference", ex);
        }
      }
    }

    this.repositoryMap = repo;
  }

  @Override
  public void save() {
    try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(dataPath))) {
      oos.writeObject(repositoryMap);
    } catch (IOException e) {
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
      throw new IDExistsException(obj.getClass().getSimpleName() + " with ID " + obj.getId() + " already exists.");
    }
    repository.put(obj.getId(), obj);
  }

  @Override
  public <T extends StoredObject> void upsert(T obj) {
    repositoryMap.computeIfAbsent(obj.getClass(), k -> new HashMap<>())
        .put(obj.getId(), obj);
  }

  @Override
  public <T extends StoredObject> void delete(T obj) {
    HashMap<String, StoredObject> repository = repositoryMap.get(obj.getClass());
    if (repository != null) {
      repository.remove(obj.getId());
      if (repository.isEmpty()) {
        repositoryMap.remove(obj.getClass());
      }
    }
  }

  @Override
  public <T extends StoredObject> void deleteById(Class<T> clazz, String id) {
    HashMap<String, StoredObject> repository = repositoryMap.get(clazz);
    if (repository != null) {
      repository.remove(id);
      if (repository.isEmpty()) {
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
    StringBuilder sb = new StringBuilder("FileStore:\n");
    for (Class<? extends StoredObject> c : repositoryMap.keySet()) {
      sb.append("\t").append(c.getSimpleName()).append(":\n");
      for (String id : repositoryMap.get(c).keySet()) {
        sb.append("\t\t").append(id).append("\n");
      }
    }
    return sb.toString();
  }
}
