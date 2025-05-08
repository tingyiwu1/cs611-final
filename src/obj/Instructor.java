package obj;

import java.util.ArrayList;

import store.Store;

/**
 * Stored object representing an instructor. Instructors are associated with a
 * course through an employment. Contains a foreign set of courses.
 * 
 * Deleting an instructor will delete all courses associated with that
 * instructor.
 */
public class Instructor extends User {

  private final ForeignSet<Course> courses = new ForeignSet<>(Course.class, "instructor"); // owned

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
