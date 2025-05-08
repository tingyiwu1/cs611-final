package model;

import java.util.Optional;

import store.Store;
import store.StoredObject;

/**
 * Stored object representing a submission. Submissions are associated with an
 * enrollment and an assignment. Contains foreign keys to the enrollment and
 * assignment.
 * 
 * We also store the submission content, and nullable grade and similarity
 * score.
 */
public class Submission extends StoredObject {

  private String content;
  private Integer grade;
  private Integer similarityScore;
  private final ForeignKey<Enrollment> enrollment = new ForeignKey<>(Enrollment.class); // owned by
  private final ForeignKey<Assignment> assignment = new ForeignKey<>(Assignment.class); // owned by

  public Submission(Store store, String assignmentId, String enrollmentId, String content) {
    super(store, enrollmentId + "-" + assignmentId);
    this.content = content;
    this.grade = null;
    this.similarityScore = null;
    this.enrollment.setId(enrollmentId);
    this.assignment.setId(assignmentId);
  }

  public Assignment getAssignment() {
    return assignment.get();
  }

  public Enrollment getEnrollment() {
    return enrollment.get();
  }

  public Student getStudent() {
    return getEnrollment().getStudent();
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
