package model;

import java.util.ArrayList;
import java.util.Date;

import store.Store;
import store.StoredObject;

/**
 * Stored object representing an assignment. Assignments are associated with a
 * category which must belong to the same course. Contains a foreign set of
 * submissions.
 */
public class Assignment extends StoredObject {
  private String name;
  private int points;
  private boolean isPublished;
  private Date dueDate;
  private final ForeignKey<Course> course = new ForeignKey<>(Course.class); // owned by
  private final ForeignKey<Category> category = new ForeignKey<>(Category.class); // owned by
  private final ForeignSet<Submission> submissions = new ForeignSet<>(Submission.class, "assignment"); // owned

  public Assignment(Store store, String id, String name, String courseId, String categoryId, int points,
      boolean isPublished, Date dueDate) {
    super(store, courseId + "-" + id);
    this.name = name;
    this.points = points;
    this.isPublished = isPublished;
    this.dueDate = dueDate;
    this.course.setId(courseId);
    this.category.setId(categoryId);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Category getCategory() {
    return category.get();
  }

  public Course getCourse() {
    return course.get();
  }

  public void setCategory(Category category) {
    if (!category.getCourseId().equals(course.getId())) {
      throw new IllegalArgumentException("Category does not belong to this course");
    }
    this.category.setId(category.getId());
  }

  public int getPoints() {
    return points;
  }

  public void setPoints(int points) {
    this.points = points;
  }

  public boolean isPublished() {
    return isPublished;
  }

  public void setPublished(boolean isPublished) {
    this.isPublished = isPublished;
  }

  public Date getDueDate() {
    return dueDate;
  }

  public void setDueDate(Date dueDate) {
    this.dueDate = dueDate;
  }

  public ArrayList<Submission> getSubmissions() {
    ArrayList<Submission> submissionsList = new ArrayList<>();
    submissions.forEach(submissionsList::add);
    return submissionsList;
  }
}
