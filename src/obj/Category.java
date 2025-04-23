package obj;

import store.Store;
import store.StoredObject;

public class Category extends StoredObject {

  private final String categoryId;
  private final Course course; // owned by
  private String name;
  private int weight;

  public Category(Store store, String categoryId, Course course, String name, int weight) {
    super(store);
    this.categoryId = categoryId;
    this.course = course;
    this.name = name;
    this.weight = weight;
  }

  public Course getCourse() {
    return course;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getWeight() {
    return weight;
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }

  @Override
  public String getId() {
    return categoryId;
  }

}
