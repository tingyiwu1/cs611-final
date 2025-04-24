package obj;

import store.StoredObject;
import store.Store;

public class Employment extends StoredObject {

  private final ForeignKey<Grader> grader = new ForeignKey<>(Grader.class); // owned by
  private final ForeignKey<Course> course = new ForeignKey<>(Course.class); // owned by

  public Employment(Store store, String graderId, String courseId) {
    super(store, graderId + "-" + courseId);
    this.grader.setId(graderId);
    this.course.setId(courseId);
  }

  public Grader getGrader() {
    return grader.get();
  }

  public Course getCourse() {
    return course.get();
  }

}
