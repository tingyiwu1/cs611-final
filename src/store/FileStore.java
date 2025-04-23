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

  private final Path dataPath;
  private final HashMap<Class<? extends StoredObject>, HashMap<String, StoredObject>> repositoryMap;

  @SuppressWarnings("unchecked")
  public FileStore(String dataDir, String fileName) {
    try {
      Files.createDirectories(Paths.get(dataDir));
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to create data directory", e);
    }
    this.dataPath = Paths.get(dataDir, fileName);

    HashMap<Class<? extends StoredObject>, HashMap<String, StoredObject>> repositoryMap;
    try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(dataPath))) {
      repositoryMap = (HashMap<Class<? extends StoredObject>, HashMap<String, StoredObject>>) ois.readObject();
    } catch (IOException e) {
      repositoryMap = new HashMap<>();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to read data file", e);
    }
    // StoredObject.store is transient, so we need to set it manually
    for (HashMap<String, StoredObject> repository : repositoryMap.values()) {
      for (StoredObject obj : repository.values()) {
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
      throw new IllegalArgumentException(
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
    }
  }

  @Override
  public <T extends StoredObject> void deleteById(Class<T> clazz, String id) {
    HashMap<String, StoredObject> repository = repositoryMap.get(clazz);
    if (repository != null) {
      repository.remove(id);
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

}
