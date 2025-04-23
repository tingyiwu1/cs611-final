package obj;

import store.StoredObject;
import store.Store;

public class Enrollment extends StoredObject {
  public static enum Status {
    ENROLLED, WAITLISTED, DROPPED
  }

  public final Student student; // owned by
  public final Course course; // owned by
  public final Status status;

  public Enrollment(Store store, Student student, Course course, Status status) {
    super(store);
    this.student = student;
    this.course = course;
    this.status = status;
  }

  @Override
  public String getId() {
    return student.getId() + course.getId();
  }
}
