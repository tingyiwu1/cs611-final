package obj;

import java.util.ArrayList;

import store.Store;

public class Grader extends User {

  private final ForeignSet<Employment> employments;

  public Grader(Store store, String id, String name) {
    super(store, id, name);
    this.employments = new ForeignSet<>(Employment.class, "grader", id);
  }

  public ArrayList<Course> getCourses() {
    ArrayList<Course> courses = new ArrayList<>();
    for (Employment employment : employments) {
      courses.add(employment.getCourse());
    }
    return courses;
  }

}
