package model;

import java.util.Date;

import store.Store;

/**
 * Factory class with utility functions for creating stored objects. Each
 * instance maintains a single store in which all store objects are created.
 */
public class StoredObjectFactory {
  private final Store store;

  public StoredObjectFactory(Store store) {
    this.store = store;
  }

  public Student createStudent(String id, String name) {
    return new Student(store, id, name);
  }

  public Instructor createInstructor(String id, String name) {
    return new Instructor(store, id, name);
  }

  public Grader createGrader(String id, String name) {
    return new Grader(store, id, name);
  }

  public Assignment createAssignment(String id, String name, String courseId, String categoryId, int points,
      boolean isPublished, Date dueDate) {
    return new Assignment(store, id, name, courseId, categoryId, points, isPublished, dueDate);
  }

  public Submission createSubmission(String assignmentId, String enrollmentId, String content) {
    return new Submission(store, assignmentId, enrollmentId, content);
  }

  public Course createCourse(String instructorId, String courseId, String code, String name, Term term,
      String description) {
    return new Course(store, instructorId, courseId, code, name, term, description);
  }

  public Enrollment createEnrollment(String studentId, String courseId, Enrollment.Status status) {
    return new Enrollment(store, studentId, courseId, status);
  }

  public Employment createEmployment(String graderId, String courseId) {
    return new Employment(store, graderId, courseId);
  }

  public Category createCategory(String categoryId, String courseId, String name, int weight) {
    return new Category(store, categoryId, courseId, name, weight);
  }

  public Term createTerm(Term.Season season, int year) {
    return new Term(season, year);
  }

}
