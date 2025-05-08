package views.editcourse.categories;

import java.util.UUID;

import model.Assignment;
import model.Category;
import model.Course;

public class ClonedCategoryRow implements EditCategoriesPanel.CategoryRow {
  private String name;
  private int weight;
  private final Category oldCategory;

  public ClonedCategoryRow(Category oldCategory) {
    this.oldCategory = oldCategory;
    this.name = oldCategory.getName();
    this.weight = oldCategory.getWeight();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public int getWeight() {
    return weight;
  }

  @Override
  public void setWeight(int weight) {
    this.weight = weight;
  }

  @Override
  public boolean canRemove() {
    return true;
  }

  @Override
  public Category save(Course course) {
    Category category = course.createCategory(UUID.randomUUID().toString(), name, weight);
    for (Assignment assignment : oldCategory.getAssignments()) {
      category.createAssignment(UUID.randomUUID().toString(), assignment.getName(), assignment.getPoints(),
          false, assignment.getDueDate());
    }
    return category;
  }

}
