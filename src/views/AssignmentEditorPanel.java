package views;

import obj.Assignment;
import obj.Category;
import obj.Course;
import store.Store;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Panel for creating or editing an Assignment.
 * Integrated into Navigator stack; uses onBack callback.
 */
public class AssignmentEditorPanel extends JPanel {
  private final MainWindow mainWindow;
  private final Course course;
  private Assignment current;
  private final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

  private JTextField nameField;
  private JComboBox<Category> categoryBox;
  private JSpinner pointsSpinner;
  private JTextField dueField;

  public static String getKey(MainWindow mainWindow,
      Course course,
      Assignment toEdit) {
    String key = "assignmentEditor:" + (toEdit != null ? toEdit.getId() : "new");
    mainWindow.getNavigator().register(key, () -> new AssignmentEditorPanel(mainWindow, course, toEdit));
    return key;
  }

  private AssignmentEditorPanel(MainWindow mainWindow,
      Course course,
      Assignment toEdit) {
    this.mainWindow = mainWindow;
    this.course = course;
    this.current = toEdit;
    initComponents();
    if (current != null)
      loadFields();
  }

  private void initComponents() {
    setLayout(new BorderLayout(10, 10));

    // Top bar with Back button and title
    JPanel top = new JPanel(new BorderLayout());
    JButton backButton = new JButton("Back");
    backButton.addActionListener(e -> mainWindow.getNavigator().back());
    top.add(backButton, BorderLayout.WEST);

    String title = (current == null) ? "Create Assignment" : "Edit Assignment";
    JLabel lbl = new JLabel(title, SwingConstants.CENTER);
    lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 20f));
    top.add(lbl, BorderLayout.CENTER);
    add(top, BorderLayout.NORTH);

    // Form
    JPanel form = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.anchor = GridBagConstraints.WEST;

    // Name
    gbc.gridx = 0;
    gbc.gridy = 0;
    form.add(new JLabel("Name:"), gbc);
    nameField = new JTextField(20);
    gbc.gridx = 1;
    form.add(nameField, gbc);

    // Category
    gbc.gridx = 0;
    gbc.gridy = 1;
    form.add(new JLabel("Category:"), gbc);
    categoryBox = new JComboBox<>(course.getCategories().toArray(new Category[0]));
    gbc.gridx = 1;
    form.add(categoryBox, gbc);

    // Points
    gbc.gridx = 0;
    gbc.gridy = 2;
    form.add(new JLabel("Points:"), gbc);
    int initPts = (current != null) ? current.getPoints() : 0;
    pointsSpinner = new JSpinner(new SpinnerNumberModel(initPts, 0, Integer.MAX_VALUE, 1));
    gbc.gridx = 1;
    form.add(pointsSpinner, gbc);

    // Due date
    gbc.gridx = 0;
    gbc.gridy = 3;
    form.add(new JLabel("Due Date (YYYY-MM-DD):"), gbc);
    dueField = new JTextField(10);
    gbc.gridx = 1;
    form.add(dueField, gbc);

    add(form, BorderLayout.CENTER);

    // Actions: Save, Delete, Publish
    JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton saveBtn = new JButton("Save");
    JButton deleteBtn = new JButton("Delete");
    JButton publishBtn = new JButton("Publish");

    saveBtn.addActionListener(e -> onSave());
    deleteBtn.addActionListener(e -> onDelete());
    publishBtn.addActionListener(e -> onPublish());

    actions.add(saveBtn);
    actions.add(deleteBtn);
    actions.add(publishBtn);
    add(actions, BorderLayout.SOUTH);
  }

  private void loadFields() {
    nameField.setText(current.getName());
    categoryBox.setSelectedItem(current.getCategory());
    pointsSpinner.setValue(current.getPoints());
    dueField.setText(fmt.format(current.getDueDate()));
  }

  private void onSave() {
    try {
      String name = nameField.getText().trim();
      Category cat = (Category) categoryBox.getSelectedItem();
      int pts = (Integer) pointsSpinner.getValue();
      Date due = fmt.parse(dueField.getText().trim());

      Store store = mainWindow.getStore();

      if (current == null) {
        // Create a new assignment
        Assignment a = course.createAssignment(
            UUID.randomUUID().toString(),
            name,
            cat,
            pts,
            false,
            due);
        store.upsert(a);
        store.save();
        JOptionPane.showMessageDialog(this,
            "Created assignment: " + a.getName(),
            "Saved", JOptionPane.INFORMATION_MESSAGE);
      } else {
        // Update existing
        current.setName(name);
        current.setCategory(cat);
        current.setPoints(pts);
        current.setDueDate(due);

        store.upsert(current);
        store.save();
        JOptionPane.showMessageDialog(this,
            "Updated assignment: " + current.getName(),
            "Saved", JOptionPane.INFORMATION_MESSAGE);
      }

      try {
        mainWindow.getNavigator().backTo(CourseViewPanel.getKey(mainWindow, course));
      } catch (NoSuchElementException e) {
        mainWindow.getNavigator().back();
      }
    } catch (ParseException ex) {
      JOptionPane.showMessageDialog(this,
          "Invalid date format", "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void onDelete() {
    if (current != null) {
      current.delete();
      mainWindow.getStore().save();
      JOptionPane.showMessageDialog(this,
          "Deleted assignment: " + current.getName(),
          "Deleted", JOptionPane.INFORMATION_MESSAGE);
      try {
        mainWindow.getNavigator().backTo(CourseViewPanel.getKey(mainWindow, course));
      } catch (NoSuchElementException e) {
        mainWindow.getNavigator().back();
      }
    }
  }

  private void onPublish() {
    if (current != null && !current.isPublished()) {
      current.setPublished(true);
      Store store = mainWindow.getStore();
      store.upsert(current);
      store.save();
      JOptionPane.showMessageDialog(this,
          "Published assignment: " + current.getName(),
          "Published", JOptionPane.INFORMATION_MESSAGE);
    }
  }
}