package obj;

import java.util.Optional;

import store.Store;
import store.StoredObject;

public class Submission extends StoredObject {

  private String content;
  private Optional<Integer> grade;
  private Optional<Integer> similarityScore;
  private final ForeignKey<Student> student;
  private final ForeignKey<Assignment> assignment;

  public Submission(Store store, String assignmentId, String studentId, String content) {
    super(store);
    this.content = content;
    this.grade = Optional.empty();
    this.similarityScore = Optional.empty();
    this.student = new ForeignKey<>(Student.class, studentId);
    this.assignment = new ForeignKey<>(Assignment.class, assignmentId);
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
    return grade.isPresent();
  }

  public Optional<Integer> getGrade() {
    return grade;
  }

  public void setGrade(int grade) {
    this.grade = Optional.of(grade);
  }

  public Optional<Integer> getSimilarityScore() {
    return similarityScore;
  }

  public void setSimilarityScore(int similarityScore) {
    this.similarityScore = Optional.of(similarityScore);
  }

  @Override
  public String getId() {
    return assignment.getId() + student.getId();
  }

}
