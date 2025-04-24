package obj;

import store.StoredObject;

import java.util.ArrayList;

import store.Store;

public class Enrollment extends StoredObject {
  public static enum Status {
    ENROLLED, DROPPED
  }

  private final ForeignKey<Student> student = new ForeignKey<>(Student.class); // owned by
  private final ForeignKey<Course> course = new ForeignKey<>(Course.class); // owned by
  private final ForeignSet<Submission> submissions = new ForeignSet<>(Submission.class, "enrollment", this); // owned
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
