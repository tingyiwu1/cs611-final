package views.editcourse.categories;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import obj.Category;
import obj.Course;

public class EditCategoriesPanel extends JPanel {
  private static final int WEIGHT_COLUMNS = 3;
  private static final int NAME_COLUMNS = 10;

  public static interface CategoryRow {
    String getName();

    void setName(String name);

    int getWeight();

    void setWeight(int weight);

    boolean canRemove();

    Category save(Course course);
  }

  public class CategoryRowPanel extends JPanel {
    public final CategoryRow row;
    public final JTextField nameField;
    public final JSpinner weightField;
    private final JButton removeButton;

    public CategoryRowPanel(CategoryRow row) {
      this.row = row;
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

      this.nameField = new JTextField(row.getName(), NAME_COLUMNS);
      this.nameField.getDocument().addDocumentListener(new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
          validateCategories();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
          validateCategories();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
          validateCategories();
        }
      });

      this.weightField = new JSpinner();
      ((JSpinner.DefaultEditor) weightField.getEditor()).getTextField().setColumns(WEIGHT_COLUMNS);
      this.weightField.setValue(row.getWeight());
      this.weightField.addChangeListener(
          (e) -> validateCategories());

      this.removeButton = new JButton("Remove");
      this.removeButton.setPreferredSize(new Dimension(100, 20));
      this.removeButton.addActionListener((e) -> onDeleteCategory(this));

      if (row.canRemove()) {
        this.removeButton.setEnabled(true);
      } else {
        this.removeButton.setText("(i)");
        this.removeButton.setToolTipText("Cannot remove category with assignments");
        this.removeButton.setEnabled(false);
      }

      JPanel rowPanel = new JPanel();
      rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.X_AXIS));
      rowPanel.add(nameField);
      rowPanel.add(weightField);
      rowPanel.add(removeButton);

      add(rowPanel);

      setMaximumSize(new Dimension(Integer.MAX_VALUE, getPreferredSize().height));
    }

    public CategoryRow getRow() {
      return row;
    }
  }

  private final JTextField newNameField;
  private final JSpinner newWeightField;
  private final JButton addCategoryButton;
  private final JLabel errorLabel;
  private final JPanel categoriesList;

  private ArrayList<CategoryRow> categories = new ArrayList<>();

  public EditCategoriesPanel() {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    this.errorLabel = new JLabel();
    errorLabel.setForeground(java.awt.Color.RED);

    this.newNameField = new JTextField(NAME_COLUMNS);
    newNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, newNameField.getPreferredSize().height));
    // this.newNameField.getDocument().addDocumentListener(new DocumentListener() {
    // @Override
    // public void insertUpdate(DocumentEvent e) {
    // validateName(newNameField.getText(), errorLabel);
    // }

    // @Override
    // public void removeUpdate(DocumentEvent e) {
    // validateName(newNameField.getText(), errorLabel);
    // }

    // @Override
    // public void changedUpdate(DocumentEvent e) {
    // validateName(newNameField.getText(), errorLabel);
    // }
    // });

    this.newWeightField = new JSpinner();
    ((JSpinner.DefaultEditor) newWeightField.getEditor()).getTextField().setColumns(WEIGHT_COLUMNS);
    this.newWeightField.setMaximumSize(newWeightField.getPreferredSize());
    this.newWeightField
        .addChangeListener((e) -> validateWeight((Integer) newWeightField.getValue()));

    this.addCategoryButton = new JButton("Add");
    this.addCategoryButton.setPreferredSize(new Dimension(100, 20));
    addCategoryButton.addActionListener(this::onAddCategory);

    // Inputs for adding a new category
    JPanel newCategoryForm = new JPanel();
    newCategoryForm.setLayout(new BoxLayout(newCategoryForm, BoxLayout.X_AXIS));
    newCategoryForm.add(new JLabel("Name:"));
    newCategoryForm.add(newNameField);
    newCategoryForm.add(new JLabel("Weight:"));
    newCategoryForm.add(newWeightField);
    newCategoryForm.add(addCategoryButton);

    JLabel titleLabel = new JLabel("Categories");
    titleLabel.setFont(titleLabel.getFont().deriveFont(16f));

    add(titleLabel);
    add(Box.createVerticalStrut(10));
    add(newCategoryForm);

    // Existing categories
    categoriesList = new JPanel();
    categoriesList.setLayout(new BoxLayout(categoriesList, BoxLayout.Y_AXIS));
    JScrollPane scrollPane = new JScrollPane(categoriesList);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setPreferredSize(new Dimension(0, 150));
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    // scrollPane.setBorder(BorderFactory.createCompoundBorder(
    // BorderFactory.createLineBorder(Color.red),
    // scrollPane.getBorder()));

    refreshCategories();

    add(scrollPane);

    add(errorLabel);

    // setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.red),
    // getBorder()));
  }

  public void setCategories(ArrayList<CategoryRow> categories) {
    this.categories = categories;
    refreshCategories();
  }

  public ArrayList<CategoryRow> getCategories() {
    return categories;
  }

  private void refreshCategories() {
    categoriesList.removeAll();
    for (CategoryRow category : categories) {
      CategoryRowPanel rowPanel = new CategoryRowPanel(category);
      categoriesList.add(rowPanel);
    }
    categoriesList.revalidate();
    categoriesList.repaint();
  }

  private boolean validateName(String name) {
    if (name.isEmpty()) {
      errorLabel.setText("Name cannot be empty.");
      return false;
    } else {
      errorLabel.setText("");
      return true;
    }
  }

  private boolean validateWeight(int weight) {
    if (weight < 0) {
      errorLabel.setText("Weight be non-negative.");
      return false;
    } else {
      errorLabel.setText("");
      return true;
    }
  }

  public boolean validateCategories() {
    for (Component row : categoriesList.getComponents()) {
      if (!(row instanceof CategoryRowPanel)) {
        continue;
      }
      CategoryRowPanel rowPanel = (CategoryRowPanel) row;
      if (!validateName(rowPanel.nameField.getText())) {
        return false;
      }
      if (!validateWeight((Integer) rowPanel.weightField.getValue())) {
        return false;
      }
      rowPanel.row.setName(rowPanel.nameField.getText());
      rowPanel.row.setWeight((Integer) rowPanel.weightField.getValue());
    }
    return true;
  }

  private void onAddCategory(ActionEvent e) {
    if (!validateName(newNameField.getText())
        || !validateWeight((Integer) newWeightField.getValue())) {
      return;
    }
    String name = newNameField.getText();
    int weight = (Integer) newWeightField.getValue();

    CategoryRow categoryRow = new NewCategoryRow(name, weight);
    categories.add(categoryRow);

    refreshCategories();

    newNameField.setText("");
    newWeightField.setValue(0);
    newNameField.requestFocus();
  }

  private void onDeleteCategory(CategoryRowPanel rowPanel) {
    if (!rowPanel.getRow().canRemove()) {
      throw new IllegalStateException("Cannot delete category with assignments");
    }
    categories.remove(rowPanel.getRow());

    refreshCategories();

    newNameField.requestFocus();

  }

}
