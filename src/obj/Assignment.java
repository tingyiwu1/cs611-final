package obj;

import java.util.ArrayList;
import java.util.Date;

import store.Store;
import store.StoredObject;

public class Assignment extends StoredObject {
  private String name;
  private int points;
  private boolean isPublished;
  private Date dueDate;
  private final ForeignKey<Course> course = new ForeignKey<>(Course.class); // owned by
  private final ForeignKey<Category> category = new ForeignKey<>(Category.class); // owned by
  private final ForeignSet<Submission> submissions = new ForeignSet<>(Submission.class, "assignment", this); // owned

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

  public static Assignment[] getSampleAssignments() {
    return new Assignment[] { new Assignment(null, "assignment1", "Assignment 1", "course1", "category1", 10, true,
        new Date()), new Assignment(null, "assignment2", "Assignment 2", "course1", "category1", 10, true, new Date()),
        new Assignment(null, "assignment3", "Assignment 3", "course1", "category1", 10, true, new Date()) };
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
