package obj;

import store.StoredObject;
import store.Store;

public class Employment extends StoredObject {

  private final ForeignKey<Grader> grader;
  private final ForeignKey<Course> course;

  public Employment(Store store, String graderId, String courseId) {
    super(store);
    this.grader = new ForeignKey<>(Grader.class, graderId);
    this.course = new ForeignKey<>(Course.class, courseId);
  }

  public Grader getGrader() {
    return grader.get();
  }

  public Course getCourse() {
    return course.get();
  }

  @Override
  public String getId() {
    return grader.getId() + course.getId();
  }
}
