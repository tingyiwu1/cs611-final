package obj;

import java.util.ArrayList;

import store.Store;

public class Grader extends User {

  private final ForeignSet<Employment> employments = new ForeignSet<>(Employment.class, "grader", this); // owned

  public Grader(Store store, String id, String name) {
    super(store, id, name);
  }

  public ArrayList<Course> getCourses() {
    ArrayList<Course> courses = new ArrayList<>();
    for (Employment employment : employments) {
      courses.add(employment.getCourse());
    }
    return courses;
  }

}
