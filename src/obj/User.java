package obj;

public class User {
  public static enum Role {
    INSTRUCTOR,
    STUDENT,
    GRADER
  }

  private final int id;
  private String name;
  private Role role;

  public User(int id, String name, Role role) {
    this.id = id;
    this.name = name;
    this.role = role;
  }
}
