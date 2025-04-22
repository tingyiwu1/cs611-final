package obj;

import java.util.Date;

public class Assignment {
  public final int id;
  public final String name;
  public final String category;
  public final int points;
  public final boolean isPublished;
  public final Date dueDate;
  public final String[] submissions;

  public Assignment(int id, String name, String category, int points, boolean isPublished, Date dueDate,
      String[] submissions) {
    this.id = id;
    this.name = name;
    this.category = category;
    this.points = points;
    this.isPublished = isPublished;
    this.dueDate = dueDate;
    this.submissions = submissions;
  }

  public static Assignment[] getSampleAssignments() {
    return new Assignment[] {
        new Assignment(1, "Homework 1", "Assignment", 100, true, new Date(),
            new String[] { "submission1", "submission2" }),
        new Assignment(2, "Homework 2", "Assignment", 100, true, new Date(),
            new String[] { "submission1", "submission2" }),
        new Assignment(3, "Homework 3", "Assignment", 100, true, new Date(),
            new String[] { "submission1", "submission2" }),
        new Assignment(4, "Homework 4", "Assignment", 100, true, new Date(),
            new String[] { "submission1", "submission2" }),
    };
  }
}
