package obj;

import store.StoredObject;

import java.util.ArrayList;

import store.Store;

/**
 * Stored object representing an enrollment, which associates a student with a
 * course. This is needed because courses and students form a many-to-many
 * relationship.
 * 
 * We also store the status of the enrollment (enrolled or dropped) in order to
 * allow students to drop courses without deleting all of their work, making it
 * easy to re-join a course if dropping was a mistake.
 * 
 * We also store submissions as a foreign set of enrollments. See
 * Submission.java
 * 
 * Deleting a course or student will delete the enrollment, and deleting the
 * enrollment will remove the association between the course and student and
 * delete all of the associated submissions. In the app, we just set the status
 * to DROPPED when dropping a student from a course instead of deleting.
 */
public class Enrollment extends StoredObject {
  public static enum Status {
    ENROLLED, DROPPED
  }

  private final ForeignKey<Student> student = new ForeignKey<>(Student.class); // owned by
  private final ForeignKey<Course> course = new ForeignKey<>(Course.class); // owned by
  private final ForeignSet<Submission> submissions = new ForeignSet<>(Submission.class, "enrollment"); // owned
  private Status status;

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

  public ArrayList<Submission> getSubmissions() {
    ArrayList<Submission> submissionsList = new ArrayList<>();
    submissions.forEach(submissionsList::add);
    return submissionsList;
  }

  public Submission createSubmission(String assignmentId, String content) {
    return new Submission(store, assignmentId, getId(), content);
  }

  public Submission createSubmission(Assignment assignment, String content) {
    return new Submission(store, assignment.getId(), getId(), content);
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public void drop() {
    setStatus(Status.DROPPED);
  }

  public void enroll() {
    setStatus(Status.ENROLLED);
  }

}
