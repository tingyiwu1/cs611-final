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

  /**
   * @param mainWindow  so you can push other screens
   * @param course      which course’s assignments to show
   * @param onBack      Runnable to pop back to the previous card
   */
  public AssignmentsScreen(MainWindow mainWindow,
                           Course course,
                           Runnable onBack) {
    this.mainWindow = mainWindow;
    this.course     = course;
    this.assignments = course != null
            ? course.getAssignments().toArray(new Assignment[0])
            : new Assignment[0];

    initComponents(onBack);
  }

  private void initComponents(Runnable onBack) {
    setLayout(new BorderLayout(10, 10));

    // ── Top bar ────────────────────────────────
    JPanel topBar = new JPanel(new BorderLayout());
    JButton back = new JButton("Back");
    back.addActionListener(e -> onBack.run());
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
      edit.addActionListener(ev -> {
        String key = "assignmentEditor:" + a.getId();
        // register dynamic screen
        mainWindow.getNavigator().register(key,
                () -> new AssignmentEditorPanel(mainWindow, course, a, onBack)
        );
        mainWindow.getNavigator().push(key);
      });
      row.add(edit);

      // Submissions button: open SubmissionsScreen
      JButton subs = new JButton("Submissions");
      subs.addActionListener(ev -> {
        String key = "submissions:" + a.getId();
        mainWindow.getNavigator().register(key,
                () -> new SubmissionsScreen(mainWindow, a, onBack)
        );
        mainWindow.getNavigator().push(key);
      });
      row.add(subs);

      list.add(row);
      list.add(Box.createRigidArea(new Dimension(0, 5)));
    });
    add(new JScrollPane(list), BorderLayout.CENTER);

    // ── Bottom bar ────────────────────────────
    JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton create = new JButton("Create New Assignment");
    create.addActionListener(e -> {
      String key = "assignmentEditor:new";
      mainWindow.getNavigator().register(key,
              () -> new AssignmentEditorPanel(mainWindow, course, null, onBack)
      );
      mainWindow.getNavigator().push(key);
    });
    bottom.add(create);
    add(bottom, BorderLayout.SOUTH);
  }
}
