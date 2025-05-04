package views.editcourse;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

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
  public static interface CategoryRow {
    String getName();

    void setName(String name);

    int getWeight();

    void setWeight(int weight);

    boolean canRemove();

    Category save(Course course);
  }

  private static interface RemoveCategoryRowAction {
    void remove(CategoryRowPanel rowPanel);
  }

  public static class CategoryRowPanel extends JPanel {
    private final CategoryRow row;
    private final JTextField nameField;
    private final JSpinner weightField;
    private final JButton removeButton;
    private final JLabel errorLabel;

    public CategoryRowPanel(CategoryRow row, RemoveCategoryRowAction removeAction) {
      this.row = row;
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

      this.errorLabel = new JLabel();
      errorLabel.setForeground(java.awt.Color.RED);
      errorLabel.setVisible(false);

      this.nameField = new JTextField(row.getName(), 20);
      this.nameField.getDocument().addDocumentListener(new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
          validateName(nameField.getText(), errorLabel);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
          validateName(nameField.getText(), errorLabel);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
          validateName(nameField.getText(), errorLabel);
        }
      });

      this.weightField = new JSpinner();
      ((JSpinner.DefaultEditor) weightField.getEditor()).getTextField().setColumns(5);
      this.weightField.setValue(row.getWeight());
      this.weightField.addChangeListener(
          (e) -> validateWeight((Integer) weightField.getValue(), errorLabel));

      this.removeButton = new JButton("Remove");
      this.removeButton.setPreferredSize(new Dimension(100, 20));
      this.removeButton.addActionListener((e) -> removeAction.remove(this));

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
      add(errorLabel);

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

  // TODO: potentially refactor to not store not store them here and just have
  // them in the CategoryRowPanels
  private ArrayList<CategoryRow> categories = new ArrayList<>();

  public EditCategoriesPanel() {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    this.errorLabel = new JLabel();
    errorLabel.setForeground(java.awt.Color.RED);
    errorLabel.setVisible(false);

    this.newNameField = new JTextField(20);
    newNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, newNameField.getPreferredSize().height));
    this.newNameField.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        validateName(newNameField.getText(), errorLabel);
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        validateName(newNameField.getText(), errorLabel);
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        validateName(newNameField.getText(), errorLabel);
      }
    });

    this.newWeightField = new JSpinner();
    ((JSpinner.DefaultEditor) newWeightField.getEditor()).getTextField().setColumns(5);
    this.newWeightField.setMaximumSize(newWeightField.getPreferredSize());
    this.newWeightField
        .addChangeListener((e) -> validateWeight((Integer) newWeightField.getValue(), errorLabel));

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

    add(errorLabel);

    add(Box.createVerticalGlue());

    // Existing categories
    categoriesList = new JPanel();
    categoriesList.setLayout(new BoxLayout(categoriesList, BoxLayout.Y_AXIS));
    JScrollPane scrollPane = new JScrollPane(categoriesList);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setPreferredSize(new Dimension(0, 150));

    refreshCategories();

    add(scrollPane);
    add(Box.createVerticalGlue());
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
      CategoryRowPanel rowPanel = new CategoryRowPanel(category, this::onDeleteCategory);
      categoriesList.add(rowPanel);
    }
    categoriesList.revalidate();
    categoriesList.repaint();
  }

  private static boolean validateName(String name, JLabel errorLabel) {
    if (name.isEmpty()) {
      if (errorLabel != null) {
        errorLabel.setText("Name cannot be empty.");
        errorLabel.setVisible(true);
      }
      return false;
    } else {
      if (errorLabel != null) {
        errorLabel.setVisible(false);
      }
      return true;
    }
  }

  private static boolean validateWeight(int weight, JLabel errorLabel) {
    if (weight < 0) {
      if (errorLabel != null) {
        errorLabel.setText("Weight be non-negative.");
        errorLabel.setVisible(true);
      }
      return false;
    } else {
      if (errorLabel != null) {
        errorLabel.setVisible(false);
      }
      return true;
    }
  }

  private void onAddCategory(ActionEvent e) {
    if (!validateName(newNameField.getText(), errorLabel)
        || !validateWeight((Integer) newWeightField.getValue(), errorLabel)) {
      return;
    }
    String name = newNameField.getText();
    int weight = (Integer) newWeightField.getValue();

    CategoryRow categoryRow = new NewCategoryRow(name, weight);
    categories.add(categoryRow);
    categoriesList.add(new CategoryRowPanel(categoryRow, this::onDeleteCategory));
    newNameField.setText("");
    newWeightField.setValue(0);
    errorLabel.setVisible(false);
  }

  private void onDeleteCategory(CategoryRowPanel rowPanel) {
    if (!rowPanel.getRow().canRemove()) {
      throw new IllegalStateException("Cannot delete category with assignments");
    }
    categories.remove(rowPanel.getRow());
    categoriesList.remove(rowPanel);
    categoriesList.revalidate();
    categoriesList.repaint();
  }

}
