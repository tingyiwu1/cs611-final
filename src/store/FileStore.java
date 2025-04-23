package store;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class FileStore implements Store {
  private static final String FILE_NAME = "data.dat";

  private final Path dataPath;
  private final HashMap<Class<? extends StoredObject>, HashMap<String, StoredObject>> repositoryMap;

  @SuppressWarnings("unchecked")
  private FileStore(String dataDir) {
    try {
      Files.createDirectories(Paths.get(dataDir));
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to create data directory", e);
    }
    this.dataPath = Paths.get(dataDir, FILE_NAME);

    HashMap<Class<? extends StoredObject>, HashMap<String, StoredObject>> repositoryMap;
    try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(dataPath))) {
      repositoryMap = (HashMap<Class<? extends StoredObject>, HashMap<String, StoredObject>>) ois.readObject();
    } catch (IOException | ClassNotFoundException e) {
      repositoryMap = new HashMap<>();
    }
    for (HashMap<String, StoredObject> repository : repositoryMap.values()) {
      for (StoredObject obj : repository.values()) {
        try {
          Field f = obj.getClass().getDeclaredField("store");
          f.setAccessible(true);
          f.set(obj, this);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
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
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends StoredObject> Optional<T> get(Class<T> clazz, String id) {
    HashMap<String, StoredObject> repository = repositoryMap.get(clazz);
    if (repository != null) {
      return Optional.ofNullable((T) repository.get(id));
    }
    return Optional.empty();
  }

  @Override
  public <T extends StoredObject> void putNew(T obj) {
    HashMap<String, StoredObject> repository = repositoryMap.computeIfAbsent(obj.getClass(), k -> new HashMap<>());
    if (repository.containsKey(obj.getId())) {
      throw new IllegalArgumentException(
          obj.getClass().getName() + " with ID " + obj.getId() + " already exists. Use upsert() instead.");
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
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'delete'");
  }

  @Override
  public <T extends StoredObject> void deleteById(Class<T> clazz, String id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
  }

  @Override
  public <T extends StoredObject> ArrayList<T> getAll(Class<T> clazz) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getAll'");
  }

}
