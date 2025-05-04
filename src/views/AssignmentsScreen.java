package views;

import obj.Assignment;
import obj.Course;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Lists all assignments for a given Course, with Edit and Submissions buttons.
 */
public class AssignmentsScreen extends JPanel {
  private final MainWindow mainWindow;
  private final Course course;
  private final Assignment[] assignments;

  public static String getKey(MainWindow mainWindow, Course course) {
    String key = "assignments:" + course.getId();
    mainWindow.getNavigator().register(key, () -> new AssignmentsScreen(mainWindow, course));
    return key;
  }

  /**
   * @param mainWindow so you can push other screens
   * @param course     which course’s assignments to show
   * @param onBack     Runnable to pop back to the previous card
   */
  private AssignmentsScreen(MainWindow mainWindow, Course course) {
    this.mainWindow = mainWindow;
    this.course = course;
    this.assignments = course != null
        ? course.getAssignments().toArray(new Assignment[0])
        : new Assignment[0];

    initComponents();
  }

  private void initComponents() {
    setLayout(new BorderLayout(10, 10));

    // ── Top bar ────────────────────────────────
    JPanel topBar = new JPanel(new BorderLayout());
    JButton back = new JButton("Back");
    back.addActionListener(e -> mainWindow.getNavigator().back());
    topBar.add(back, BorderLayout.WEST);

    JLabel title = new JLabel("Assignments – " + course.getCode(),
        SwingConstants.CENTER);
    title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
    topBar.add(title, BorderLayout.CENTER);

    add(topBar, BorderLayout.NORTH);

    // ── Assignment list ───────────────────────
    JPanel list = new JPanel();
    list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
    Arrays.stream(assignments).forEach(a -> {
      JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
      row.add(new JLabel(a.getName()));
      row.add(Box.createHorizontalStrut(10));
      row.add(new JLabel(a.getPoints() + " pts"));
      row.add(Box.createHorizontalStrut(10));
      row.add(new JLabel(a.getDueDate().toString()));
      row.add(Box.createHorizontalGlue());

      // Edit button: open AssignmentEditorPanel
      JButton edit = new JButton("Edit");
      edit.addActionListener(ev -> mainWindow.getNavigator().push(AssignmentEditorPanel.getKey(mainWindow, course, a)));
      row.add(edit);

      // Submissions button: open SubmissionsScreen
      JButton subs = new JButton("Submissions");
      subs.addActionListener(ev -> mainWindow.getNavigator().push(SubmissionsScreen.getKey(mainWindow, a)));
      row.add(subs);

      list.add(row);
      list.add(Box.createRigidArea(new Dimension(0, 5)));
    });
    add(new JScrollPane(list), BorderLayout.CENTER);

    // ── Bottom bar ────────────────────────────
    JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton create = new JButton("Create New Assignment");
    create.addActionListener(
        ev -> mainWindow.getNavigator().push(AssignmentEditorPanel.getKey(mainWindow, course, null)));
    bottom.add(create);
    add(bottom, BorderLayout.SOUTH);
  }
}
