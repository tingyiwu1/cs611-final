package obj;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import store.Store;
import store.StoredObject;

// owned by course
public class Assignment extends StoredObject {
  public final String id;
  public String name;
  public Category category;
  public int points;
  public boolean isPublished;
  public Date dueDate;
  public HashMap<String, Submission> submissions; // deletes

  public Assignment(Store store, String id, String name, Category category, int points, boolean isPublished,
      Date dueDate) {
    super(store);
    this.id = id;
    this.name = name;
    this.category = category;
    this.points = points;
    this.isPublished = isPublished;
    this.dueDate = dueDate;
    this.submissions = new HashMap<>();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
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
    return new ArrayList<>(submissions.values());
  }

  @Override
  public String getId() {
    return id;
  }

  // public static Assignment[] getSampleAssignments() {
  // return new Assignment[] {
  // new Assignment("1", "Homework 1", "Assignment", 100, true, new Date(),
  // new String[] { "submission1", "submission2" }),
  // new Assignment("2", "Homework 2", "Assignment", 100, true, new Date(),
  // new String[] { "submission1", "submission2" }),
  // new Assignment("3", "Homework 3", "Assignment", 100, true, new Date(),
  // new String[] { "submission1", "submission2" }),
  // new Assignment("4", "Homework 4", "Assignment", 100, true, new Date(),
  // new String[] { "submission1", "submission2" }),
  // };
  // }

}
