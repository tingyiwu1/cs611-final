package views.assignments;

import obj.Assignment;
import obj.Category;
import obj.Course;
import store.Store;
import views.MainWindow;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Panel for creating or editing an Assignment.
 * Integrated into Navigator stack; uses onBack callback.
 */
public class AssignmentEditorPanel extends JPanel {
  public static enum EditMode {
    CREATE,
    EDIT
  }

  private final MainWindow mainWindow;
  private final EditMode mode;
  private final Course course;
  private Assignment current;
  private final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd hh:mm a");

  private JTextField nameField;
  private JComboBox<Category> categoryBox;
  private JSpinner pointsSpinner;
  private JTextField dueField;
  private JLabel errorLabel;
  private JPanel actions;

  public static String getCreateKey(MainWindow mainWindow,
      Course course) {
    String key = "createAssignment:" + course.getId();
    mainWindow.getNavigator().register(key, () -> new AssignmentEditorPanel(mainWindow, course, EditMode.CREATE, null));
    return key;
  }

  public static String getEditKey(MainWindow mainWindow,
      Course course,
      Assignment toEdit) {
    String key = "editAssignment:" + course.getId() + ":" + toEdit.getId();
    mainWindow.getNavigator().register(key, () -> new AssignmentEditorPanel(mainWindow, course, EditMode.EDIT, toEdit));
    return key;
  }

  private AssignmentEditorPanel(MainWindow mainWindow,
      Course course,
      EditMode mode,
      Assignment toEdit) {
    this.mainWindow = mainWindow;
    this.course = course;

    this.mode = mode;
    if ((mode == EditMode.CREATE) != (toEdit == null)) {
      throw new IllegalArgumentException("Edit mode must be CREATE if toEdit is null");
    }
    this.current = toEdit;

    initComponents();
    if (current != null) {
      loadFields();
    }
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
    nameField.getDocument().addDocumentListener(new ValidateDocumentListener());
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
    form.add(new JLabel("Due Date (eg. 2025-01-01 12:00 AM):"), gbc);
    dueField = new JTextField(20);
    dueField.getDocument().addDocumentListener(new ValidateDocumentListener());
    gbc.gridx = 1;
    form.add(dueField, gbc);

    add(form, BorderLayout.CENTER);

    this.errorLabel = new JLabel();
    errorLabel.setForeground(Color.RED);
    errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    // Actions: Save, Delete, Publish
    actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    refreshActions();

    add(actions, BorderLayout.SOUTH);
  }

  private ArrayList<JButton> saveActions = new ArrayList<>();

  private void refreshActions() {
    assert (actions != null) : "Actions panel is null";
    actions.removeAll();
    saveActions.clear();

    actions.add(errorLabel);

    if (mode == EditMode.EDIT) {
      JButton saveBtn = new JButton("Save");
      saveBtn.addActionListener(e -> onSave(current.isPublished()));
      actions.add(saveBtn);
      saveActions.add(saveBtn);

      JButton publishBtn = new JButton(current.isPublished() ? "Unpublish" : "Publish");
      publishBtn.addActionListener(e -> onTogglePublish());
      actions.add(publishBtn);
      saveActions.add(publishBtn);

      JButton deleteBtn = new JButton("Delete");
      deleteBtn.addActionListener(e -> onDelete());
      actions.add(deleteBtn);
    } else {
      JButton saveDraftBtn = new JButton("Save as Draft");
      saveDraftBtn.addActionListener(e -> onSave(false));
      actions.add(saveDraftBtn);
      saveActions.add(saveDraftBtn);

      JButton publishBtn = new JButton("Save and Publish");
      publishBtn.addActionListener(e -> onSave(true));
      actions.add(publishBtn);
      saveActions.add(publishBtn);
    }

    JButton cancelBtn = new JButton("Cancel");
    cancelBtn.addActionListener(e -> mainWindow.getNavigator().back());
    actions.add(cancelBtn);

    setSaveActionsEnabled(validateInputs());

    actions.revalidate();
    actions.repaint();
  }

  private void setSaveActionsEnabled(boolean enabled) {
    for (JButton btn : saveActions) {
      btn.setEnabled(enabled);
    }
  }

  private void loadFields() {
    assert (current != null) : "Current assignment is null";

    nameField.setText(current.getName());
    categoryBox.setSelectedItem(current.getCategory());
    pointsSpinner.setValue(current.getPoints());
    dueField.setText(fmt.format(current.getDueDate()));
  }

  private boolean validateInputs() {
    if (nameField.getText().trim().isEmpty()) {
      errorLabel.setText("Name cannot be empty");
      return false;
    }

    if (pointsSpinner.getValue() == null || (Integer) pointsSpinner.getValue() < 0) {
      errorLabel.setText("Points must be a non-negative number");
      return false;
    }

    try {
      fmt.parse(dueField.getText().trim());
    } catch (ParseException e) {
      errorLabel.setText("Invalid date format");
      return false;
    }

    errorLabel.setText("");
    return true;
  }

  private class ValidateDocumentListener implements DocumentListener {
    @Override
    public void insertUpdate(DocumentEvent e) {
      setSaveActionsEnabled(validateInputs());
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
      setSaveActionsEnabled(validateInputs());
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
      setSaveActionsEnabled(validateInputs());
    }
  }

  private void onSave(boolean publish) {
    try {
      String name = nameField.getText().trim();
      Category cat = (Category) categoryBox.getSelectedItem();
      int pts = (Integer) pointsSpinner.getValue();
      Date due = fmt.parse(dueField.getText().trim());

      Store store = mainWindow.getStore();

      if (mode == EditMode.CREATE) {
        // Create a new assignment
        Assignment a = course.createAssignment(
            UUID.randomUUID().toString(),
            name,
            cat,
            pts,
            publish,
            due);
        store.upsert(a);
        store.save();
      } else {
        // Update existing
        current.setName(name);
        current.setCategory(cat);
        current.setPoints(pts);
        current.setDueDate(due);
        current.setPublished(publish);

        store.upsert(current);
        store.save();
      }

      try {
        mainWindow.getNavigator().backTo(AssignmentsScreen.getKey(mainWindow, course));
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
      int confirm = JOptionPane.showConfirmDialog(this,
          "Are you sure you want to delete this assignment?",
          "Delete Assignment", JOptionPane.YES_NO_OPTION);
      if (confirm != JOptionPane.YES_OPTION) {
        return;
      }
      current.delete();
      mainWindow.getStore().save();
      // JOptionPane.showMessageDialog(this,
      // "Deleted assignment: " + current.getName(),
      // "Deleted", JOptionPane.INFORMATION_MESSAGE);
      try {
        mainWindow.getNavigator().backTo(AssignmentsScreen.getKey(mainWindow, course));
      } catch (NoSuchElementException e) {
        mainWindow.getNavigator().back();
      }
    }
  }

  private void onTogglePublish() {
    if (current != null) {
      current.setPublished(!current.isPublished());
      mainWindow.getStore().save();
      refreshActions();
    }
  }
}