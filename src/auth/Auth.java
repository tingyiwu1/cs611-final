package auth;

import java.util.Optional;

import obj.Grader;
import obj.Instructor;
import obj.Student;
import obj.User;
import store.Store;

/**
 * Auth class for managing user authentication and authorization.
 * 
 * This class provides methods to log in, log out, and check the current user's
 * type and information.
 */
public class Auth {
  public static enum UserType {
    INSTRUCTOR, STUDENT, GRADER
  }

  private final Store store;

  private Optional<User> user = Optional.empty();

  public Auth(Store store) {
    this.store = store;
  }

  public boolean isLoggedIn() {
    return user.isPresent();
  }

  public Optional<User> login(String id) {
    if (isLoggedIn()) {
      throw new IllegalStateException("Already logged in");
    }
    Optional<Instructor> instructor = store.get(Instructor.class, id);
    if (instructor.isPresent()) {
      user = Optional.of(instructor.get());
      return user;
    }
    Optional<Student> student = store.get(Student.class, id);
    if (student.isPresent()) {
      user = Optional.of(student.get());
      return user;
    }
    Optional<Grader> grader = store.get(Grader.class, id);
    if (grader.isPresent()) {
      user = Optional.of(grader.get());
      return user;
    }
    return Optional.empty();
  }

  public void logout() {
    user = Optional.empty();
  }

  public UserType getUserType() {
    if (!isLoggedIn()) {
      throw new IllegalStateException("Not logged in");
    }
    if (user.get() instanceof Instructor) {
      return UserType.INSTRUCTOR;
    } else if (user.get() instanceof Student) {
      return UserType.STUDENT;
    } else if (user.get() instanceof Grader) {
      return UserType.GRADER;
    } else {
      throw new IllegalStateException("Unknown user type");
    }
  }

  public User getUser() {
    if (!isLoggedIn()) {
      throw new IllegalStateException("Not logged in");
    }
    return user.get();
  }

  public Optional<Instructor> getInstructor() {
    User currentUser = getUser();
    if (currentUser instanceof Instructor) {
      return Optional.of((Instructor) currentUser);
    }
    return Optional.empty();
  }

  public Optional<Student> getStudent() {
    User currentUser = getUser();
    if (currentUser instanceof Student) {
      return Optional.of((Student) currentUser);
    }
    return Optional.empty();
  }

  public Optional<Grader> getGrader() {
    User currentUser = getUser();
    if (currentUser instanceof Grader) {
      return Optional.of((Grader) currentUser);
    }
    return Optional.empty();
  }

}
