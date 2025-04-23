package obj;

import store.Store;
import store.StoredObject;

public class Category extends StoredObject {

  private final String categoryId;
  private String name;
  private int weight;
  private final ForeignKey<Course> course;

  public Category(Store store, String categoryId, String courseId, String name, int weight) {
    super(store);
    this.categoryId = categoryId;
    this.name = name;
    this.weight = weight;
    this.course = new ForeignKey<>(Course.class, courseId);
  }

  public String getCourseId() {
    return course.getId();
  }

  public Course getCourse() {
    return course.get();
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
