package model;

import java.util.ArrayList;
import java.util.Optional;

import store.Store;

/**
 * Stored object representing a student. Students are associated with a course
 * through an enrollment. Contains a foreign set of enrollments.
 * 
 * Deleting a student will delete all enrollments associated with that student.
 */
public class Student extends User {

  private final ForeignSet<Enrollment> enrollments = new ForeignSet<>(Enrollment.class, "student"); // owned

  public Student(Store store, String id, String name) {
    super(store, id, name);
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

  public Optional<Enrollment> getEnrollment(String courseId) {
    return store.get(Enrollment.class, getId() + "-" + courseId);
  }

  public Enrollment createEnrollment(String courseId, Enrollment.Status status) {
    return new Enrollment(store, getId(), courseId, status);
  }

  public Enrollment createEnrollment(Course course, Enrollment.Status status) {
    return createEnrollment(course.getId(), status);
  }

  public void enrollInCourse(String courseId) {
    Optional<Enrollment> enrollment = getEnrollment(courseId);
    if (enrollment.isPresent()) {
      enrollment.get().setStatus(Enrollment.Status.ENROLLED);
    } else {
      createEnrollment(courseId, Enrollment.Status.ENROLLED);
    }
  }

  public void enrollInCourse(Course course) {
    enrollInCourse(course.getId());
  }

  public void dropEnrollment(String courseId) {
    Optional<Enrollment> enrollment = getEnrollment(courseId);
    if (enrollment.isPresent()) {
      enrollment.get().setStatus(Enrollment.Status.DROPPED);
    }
  }

  public void dropEnrollment(Course course) {
    dropEnrollment(course.getId());
  }

}
