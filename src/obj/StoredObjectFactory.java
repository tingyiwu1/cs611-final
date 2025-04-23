package obj;

import java.util.Date;

import store.Store;

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

  public Assignment createAssignment(String id, String name, Category category, int points, boolean isPublished,
      Date dueDate) {
    return new Assignment(store, id, name, category, points, isPublished, dueDate);
  }

  public Submission createSubmission(Assignment assignment, Student student, String content) {
    return new Submission(store, assignment, student, content);
  }

  public Course createCourse(String courseId, String code, String name, Term term, String description) {
    return new Course(store, courseId, code, name, term, description);
  }

  public Enrollment createEnrollment(Student student, Course course, Enrollment.Status status) {
    return new Enrollment(store, student, course, status);
  }

  public Category createCategory(String categoryId, Course course, String name, int weight) {
    return new Category(store, categoryId, course, name, weight);
  }

  public Term createTerm(Term.Season season, int year) {
    return new Term(store, season, year);
  }

}
