package views.editcourse.categories;

import java.util.UUID;

import obj.Category;
import obj.Course;

public class NewCategoryRow implements EditCategoriesPanel.CategoryRow {
  private String name;
  private int weight;

  public NewCategoryRow(String name, int weight) {
    this.name = name;
    this.weight = weight;
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
    return course.createCategory(UUID.randomUUID().toString(), name, weight);
  }
}
