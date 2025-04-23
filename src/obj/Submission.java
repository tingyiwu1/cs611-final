package obj;

import java.util.Optional;

import store.Store;
import store.StoredObject;

public class Submission extends StoredObject {

  private String content;
  private Integer grade;
  private Integer similarityScore;
  private final ForeignKey<Student> student = new ForeignKey<>(Student.class); // owned by
  private final ForeignKey<Assignment> assignment = new ForeignKey<>(Assignment.class); // owned by

  public Submission(Store store, String assignmentId, String studentId, String content) {
    super(store, studentId + "-" + assignmentId);
    this.content = content;
    this.grade = null;
    this.similarityScore = null;
    this.student.setId(studentId);
    this.assignment.setId(assignmentId);
  }

  public Assignment getAssignment() {
    return assignment.get();
  }

  public Student getStudent() {
    return student.get();
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public boolean isGraded() {
    return grade != null;
  }

  public Optional<Integer> getGrade() {
    return Optional.ofNullable(grade);
  }

  public void setGrade(int grade) {
    this.grade = grade;
  }

  public Optional<Integer> getSimilarityScore() {
    return Optional.ofNullable(similarityScore);
  }

  public void setSimilarityScore(int similarityScore) {
    this.similarityScore = similarityScore;
  }

}
