package views.editcourse;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import model.Category;
import model.Course;
import model.Grader;
import model.Term;
import views.MainWindow;
import views.course.CourseViewScreen;
import views.editcourse.categories.ClonedCategoryRow;
import views.editcourse.categories.EditCategoriesPanel;
import views.editcourse.categories.ExistingCategoryRow;
import views.editcourse.categories.NewCategoryRow;

public class EditCourseScreen extends JPanel {
  public static enum EditMode {
    EDIT,
    CREATE,
    CLONE
  }

  private final MainWindow mainWindow;
  private EditMode mode;
  private Course target;

  private JTextField courseNumberField;
  private JTextField courseNameField;
  private JTextField courseDescriptionField;
  private JComboBox<Term.Season> seasonBox;
  private JTextField yearField;
  private JLabel errorLabel;

  private JButton saveButton;

  private EditCategoriesPanel categoriesPanel;
  private EditGradersPanel gradersPanel;

  public static String getCreateKey(MainWindow mainWindow) {
    String key = "createCourse";
    mainWindow.getNavigator().register(key, () -> create(mainWindow));
    return key;
  }

  public static String getEditKey(MainWindow mainWindow, Course target) {
    String key = "editCourse:" + target.getId();
    mainWindow.getNavigator().register(key, () -> edit(mainWindow, target));
    return key;
  }

  public static String getCloneKey(MainWindow mainWindow, Course target) {
    String key = "cloneCourse:" + target.getId();
    mainWindow.getNavigator().register(key, () -> clone(mainWindow, target));
    return key;
  }

  public static EditCourseScreen create(MainWindow mainWindow) {
    return new EditCourseScreen(mainWindow, EditMode.CREATE, null);
  }

  public static EditCourseScreen edit(MainWindow mainWindow, Course target) {
    return new EditCourseScreen(mainWindow, EditMode.EDIT, target);
  }

  public static EditCourseScreen clone(MainWindow mainWindow, Course target) {
    return new EditCourseScreen(mainWindow, EditMode.CLONE, target);
  }

  private EditCourseScreen(MainWindow mainWindow,
      EditMode mode,
      Course target) {
    this.mainWindow = mainWindow;

    this.mode = mode;
    if ((mode == EditMode.CREATE) != (target == null)) {
      throw new IllegalArgumentException("CREATE mode requires null target, EDIT and CLONE require non-null target");
    }
    this.target = target;

    initComponents();
    if (target != null) {
      loadFields();
    }

    saveButton.setEnabled(validateInputs());
  }

  private void initComponents() {
    setLayout(new BorderLayout(10, 10));

    // Top bar with Back button and title
    JPanel top = new JPanel(new BorderLayout());
    JButton backButton = new JButton("Back");
    backButton.addActionListener(e -> mainWindow.getNavigator().back());
    top.add(backButton, BorderLayout.WEST);

    String title = (mode == EditMode.EDIT) ? "Edit Course"
        : (mode == EditMode.CREATE) ? "Create Course" : "Clone Course";
    JLabel lbl = new JLabel(title, SwingConstants.CENTER);
    lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 20f));
    top.add(lbl, BorderLayout.CENTER);
    add(top, BorderLayout.NORTH);

    // Form
    JPanel form = new JPanel();
    form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

    // createInfoSection();
    JPanel infoSection = createInfoSection();
    infoSection.setAlignmentX(Component.CENTER_ALIGNMENT);
    // infoSection.setBorder(BorderFactory.createCompoundBorder(
    // BorderFactory.createLineBorder(Color.red),
    // infoSection.getBorder()));
    infoSection.setMaximumSize(infoSection.getPreferredSize());

    form.add(infoSection);

    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    splitPane.setResizeWeight(0.5);

    this.categoriesPanel = new EditCategoriesPanel();
    splitPane.setLeftComponent(categoriesPanel);

    this.gradersPanel = new EditGradersPanel(mainWindow);
    splitPane.setRightComponent(gradersPanel);

    splitPane.setAlignmentX(Component.CENTER_ALIGNMENT);
    splitPane.setBorder(BorderFactory.createEmptyBorder(5, 50, 5, 50));
    // splitPane.setBorder(BorderFactory.createCompoundBorder(
    // BorderFactory.createLineBorder(Color.red),
    // splitPane.getBorder()));
    splitPane.setMaximumSize(splitPane.getPreferredSize());

    form.add(splitPane);

    form.add(Box.createVerticalGlue());

    JScrollPane scrollPane = new JScrollPane(form);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    add(scrollPane, BorderLayout.CENTER);

    // Actions
    JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    this.saveButton = new JButton((mode == EditMode.CREATE) ? "Create" : "Save");
    saveButton.addActionListener(this::onSave);
    saveButton.setEnabled(false);

    JButton cancelBtn = new JButton("Cancel");
    cancelBtn.addActionListener(this::onCancel);

    this.errorLabel = new JLabel();
    errorLabel.setForeground(Color.RED);
    errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    actions.add(errorLabel);
    actions.add(Box.createHorizontalStrut(10));
    actions.add(Box.createHorizontalGlue());
    actions.add(Box.createHorizontalStrut(10));
    actions.add(saveButton);
    actions.add(cancelBtn);

    add(actions, BorderLayout.SOUTH);
  }

  private void loadFields() {
    assert target != null : "loadFields() called with null target";

    courseNumberField.setText(target.getCode());
    courseNameField.setText(target.getName());
    courseDescriptionField.setText(target.getDescription());

    // term shouldn't be set on clone
    if (mode == EditMode.EDIT) {
      seasonBox.setSelectedItem(target.getTerm().getSeason());
      yearField.setText(String.valueOf(target.getTerm().getYear()));
    }

    ArrayList<EditCategoriesPanel.CategoryRow> categories = new ArrayList<>();

    target.getCategories().forEach((c) -> {
      categories
          .add(mode == EditMode.EDIT ? new ExistingCategoryRow(c)
              : mode == EditMode.CLONE ? new ClonedCategoryRow(c)
                  : new NewCategoryRow(c.getName(), c.getWeight()));
    });
    categoriesPanel.setCategories(categories);

    gradersPanel.setGraders(target.getGraders());
  }

  private boolean validateInputs() {
    if (courseNumberField.getText().trim().isEmpty()) {
      errorLabel.setText("Course number cannot be empty");
      return false;
    }

    if (courseNameField.getText().trim().isEmpty()) {
      errorLabel.setText("Course name cannot be empty");
      return false;
    }

    try {
      Integer.parseInt(yearField.getText().trim());
    } catch (NumberFormatException e) {
      errorLabel.setText("Course year must be a number");
      return false;
    }

    errorLabel.setText("");
    return true;
  }

  private void onSave(ActionEvent e) {
    if (!categoriesPanel.validateCategories()) {
      JOptionPane.showMessageDialog(this, "Please fix the errors in the categories before saving",
          "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    String code = courseNumberField.getText().trim();
    String name = courseNameField.getText().trim();
    String description = courseDescriptionField.getText().trim();
    Term.Season season = (Term.Season) seasonBox.getSelectedItem();
    int year = Integer.parseInt(yearField.getText().trim());

    if (mode == EditMode.EDIT) {
      target.setCode(code);
      target.setName(name);
      target.setDescription(description);
      target.setTerm(new Term(season, year));

      HashSet<Category> categoriesToDelete = new HashSet<>(target.getCategories());
      categoriesPanel.getCategories().forEach((row) -> categoriesToDelete.remove(row.save(target)));
      categoriesToDelete.forEach((c) -> c.delete());

      HashSet<Grader> toRemoveGraders = new HashSet<>(target.getGraders());
      ArrayList<Grader> toAddGraders = new ArrayList<>();

      gradersPanel.getGraders().forEach((g) -> {
        if (toRemoveGraders.contains(g)) {
          toRemoveGraders.remove(g);
        } else {
          toAddGraders.add(g);
        }
      });

      toRemoveGraders.forEach((g) -> g.getEmployment(target)
          .orElseThrow(() -> new IllegalStateException("Trying to remove grader not employed for course"))
          .delete());
      toAddGraders.forEach((g) -> target.createEmployment(g));

      mainWindow.getStore().save();
      try {
        mainWindow.getNavigator().backTo(CourseViewScreen.getKey(mainWindow, target));
      } catch (NoSuchElementException ex) {
        // This panel should only be reachable from CourseViewPanel, so shouldn't happen
        System.err.println("EditCoursePanel: No CourseViewPanel found in navigator stack");
        mainWindow.getNavigator().back();
      }
    } else {
      Course newCourse = mainWindow.getAuth().getInstructor().get()
          .createCourse(UUID.randomUUID().toString(), code, name, new Term(season, year), description);

      categoriesPanel.getCategories().forEach((row) -> row.save(newCourse));

      gradersPanel.getGraders().forEach((g) -> newCourse.createEmployment(g));

      mainWindow.getStore().save();
      mainWindow.getNavigator().replace(CourseViewScreen.getKey(mainWindow, newCourse));
    }
  }

  private void onCancel(ActionEvent e) {
    mainWindow.getNavigator().back();
  }

  private class ValidateDocumentListener implements DocumentListener {
    @Override
    public void insertUpdate(DocumentEvent e) {
      saveButton.setEnabled(validateInputs());
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
      saveButton.setEnabled(validateInputs());
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
      saveButton.setEnabled(validateInputs());
    }
  }

  private JPanel createInfoSection() {
    JPanel infoSection = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new java.awt.Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.WEST;

    // course number
    gbc.gridx = 0;
    gbc.gridy = 0;
    infoSection.add(new JLabel("Course Number:"), gbc);
    courseNumberField = new JTextField(20);
    courseNumberField.getDocument().addDocumentListener(new ValidateDocumentListener());
    gbc.gridx = 1;
    infoSection.add(courseNumberField, gbc);

    // course name
    gbc.gridx = 0;
    gbc.gridy = 1;
    infoSection.add(new JLabel("Course Name:"), gbc);
    courseNameField = new JTextField(20);
    courseNameField.getDocument().addDocumentListener(new ValidateDocumentListener());
    gbc.gridx = 1;
    infoSection.add(courseNameField, gbc);

    // course description
    gbc.gridx = 0;
    gbc.gridy = 2;
    infoSection.add(new JLabel("Course Description:"), gbc);
    courseDescriptionField = new JTextField(20);
    gbc.gridx = 1;
    infoSection.add(courseDescriptionField, gbc);

    // course season
    gbc.gridx = 0;
    gbc.gridy = 3;
    infoSection.add(new JLabel("Course Season:"), gbc);
    seasonBox = new JComboBox<>(Term.Season.values());
    gbc.gridx = 1;
    infoSection.add(seasonBox, gbc);
    // course year
    gbc.gridx = 0;
    gbc.gridy = 4;
    infoSection.add(new JLabel("Course Year:"), gbc);
    yearField = new JTextField(20);
    yearField.getDocument().addDocumentListener(new ValidateDocumentListener());
    gbc.gridx = 1;
    infoSection.add(yearField, gbc);

    return infoSection;
  }

}
