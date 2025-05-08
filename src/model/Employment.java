package model;

import store.StoredObject;
import store.Store;

/**
 * Stored object representing an employment, which associates a grader with a
 * course. This is needed because courses and graders form a many-to-many
 * relationship. Deleting a course or grader will delete the employment, and
 * deleting the employment will remove the association between the course and
 * grader.
 */
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
