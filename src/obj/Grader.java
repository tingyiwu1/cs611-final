package obj;

import java.util.ArrayList;
import java.util.Optional;

import store.Store;

/**
 * Stored object representing a grader. Graders are associated with a course
 * through an employment. Contains a foreign set of employments.
 * 
 * Deleting a grader will delete all employments associated with that grader.
 */
public class Grader extends User {

  private final ForeignSet<Employment> employments = new ForeignSet<>(Employment.class, "grader"); // owned

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

  public Optional<Employment> getEmployment(String courseId) {
    return store.get(Employment.class, getId() + "-" + courseId);
  }

  public Optional<Employment> getEmployment(Course course) {
    return getEmployment(course.getId());
  }

}
