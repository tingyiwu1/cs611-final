package obj;

import java.util.ArrayList;

import store.Store;

public class Instructor extends User {

  private final ForeignSet<Course> courses;

  public Instructor(Store store, String id, String name) {
    super(store, id, name);
    this.courses = new ForeignSet<>(Course.class, "instructor", id);
  }

  public ArrayList<Course> getCourses() {
    ArrayList<Course> coursesList = new ArrayList<>();
    courses.forEach(coursesList::add);
    return coursesList;
  }
}
