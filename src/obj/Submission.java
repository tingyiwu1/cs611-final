package obj;

import java.util.Optional;

import store.Store;
import store.StoredObject;

public class Submission extends StoredObject {

  private final Assignment assignment; // owned by
  private final Student student; // owned by
  private String content;
  private Optional<Integer> grade;
  private Optional<Integer> similarityScore;

  public Submission(Store store, Assignment assignment, Student student, String content) {
    super(store);
    this.assignment = assignment;
    this.student = student;
    this.content = content;
    this.grade = Optional.empty();
    this.similarityScore = Optional.empty();
  }

  public Assignment getAssignment() {
    return assignment;
  }

  public Student getStudent() {
    return student;
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
