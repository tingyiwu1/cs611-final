package views.editcourse.categories;

import obj.Category;
import obj.Course;

public class ExistingCategoryRow implements EditCategoriesPanel.CategoryRow {
  private String name;
  private int weight;
  private final Category category;

  public ExistingCategoryRow(Category category) {
    this.category = category;
    this.name = category.getName();
    this.weight = category.getWeight();
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
    return category.getAssignments().isEmpty();
  }

  @Override
  public Category save(Course course) {
    if (category.getCourse() != course) {
      throw new IllegalStateException("Category does not belong to this course");
    }

    category.setName(name);
    category.setWeight(weight);
    return category;
  }
}