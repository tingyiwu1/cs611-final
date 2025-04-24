package obj;

import java.util.ArrayList;

import store.Store;

public class Instructor extends User {

  private final ForeignSet<Course> courses = new ForeignSet<>(Course.class, "instructor", this); // owned

  public Instructor(Store store, String id, String name) {
    super(store, id, name);
  }

  public ArrayList<Course> getCourses() {
    ArrayList<Course> coursesList = new ArrayList<>();
    courses.forEach(coursesList::add);
    return coursesList;
  }

  public Course createCourse(String courseId, String code, String name, Term term, String description) {
    return new Course(store, getId(), courseId, code, name, term, description);
  }
}
