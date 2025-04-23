package obj;

import store.StoredObject;
import store.Store;

public class Enrollment extends StoredObject {
  public static enum Status {
    ENROLLED, WAITLISTED, DROPPED
  }

  private final ForeignKey<Student> student;
  private final ForeignKey<Course> course;
  private final Status status;

  public Enrollment(Store store, String studentId, String courseId, Status status) {
    super(store);
    this.student = new ForeignKey<>(Student.class, studentId);
    this.course = new ForeignKey<>(Course.class, courseId);
    this.status = status;
  }

  public Student getStudent() {
    return student.get();
  }

  public Course getCourse() {
    return course.get();
  }

  public Status getStatus() {
    return status;
  }

  @Override
  public String getId() {
    return student.getId() + course.getId();
  }
}
