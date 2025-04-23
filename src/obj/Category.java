package obj;

import java.util.ArrayList;

import store.Store;
import store.StoredObject;

public class Category extends StoredObject {

  private String name;
  private int weight;
  private final ForeignKey<Course> course = new ForeignKey<>(Course.class); // owned by
  private final ForeignSet<Assignment> assignments = new ForeignSet<>(Assignment.class, "category", this); // owned

  public Category(Store store, String id, String courseId, String name, int weight) {
    super(store, courseId + "-" + id);
    this.name = name;
    this.weight = weight;
    this.course.setId(courseId);
  }

  public String getCourseId() {
    return course.getId();
  }

  public Course getCourse() {
    return course.get();
  }

  public ArrayList<Assignment> getAssignments() {
    ArrayList<Assignment> assignmentsList = new ArrayList<>();
    assignments.forEach(assignmentsList::add);
    return assignmentsList;
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
}
