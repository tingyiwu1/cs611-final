package obj;

import java.util.ArrayList;

import store.Store;

public class Student extends User {

  private final ForeignSet<Enrollment> enrollments;

  public Student(Store store, String id, String name) {
    super(store, id, name);
    this.enrollments = new ForeignSet<>(Enrollment.class, "student", id);
  }

  public ArrayList<Enrollment> getEnrollments() {
    ArrayList<Enrollment> enrollmentsList = new ArrayList<>();
    enrollments.forEach(enrollmentsList::add);
    return enrollmentsList;
  }

  public ArrayList<Course> getEnrolledCourses() {
    ArrayList<Course> courses = new ArrayList<>();
    for (Enrollment enrollment : enrollments) {
      if (enrollment.getStatus() == Enrollment.Status.ENROLLED) {
        courses.add(enrollment.getCourse());
      }
    }
    return courses;
  }

}
