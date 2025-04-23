package obj;

import store.StoredObject;
import store.Store;

public class Enrollment extends StoredObject {
  public static enum Status {
    ENROLLED, WAITLISTED, DROPPED
  }

  private final ForeignKey<Student> student = new ForeignKey<>(Student.class); // owned by
  private final ForeignKey<Course> course = new ForeignKey<>(Course.class); // owned by
  private final Status status;

  public Enrollment(Store store, String studentId, String courseId, Status status) {
    super(store, studentId + "-" + courseId);
    this.student.setId(studentId);
    this.course.setId(courseId);
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

}
