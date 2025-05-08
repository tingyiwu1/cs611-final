package views.assignments;

import views.MainWindow;
import views.assignments.submissions.SubmissionsScreen;

import javax.swing.*;

import model.Assignment;
import model.Course;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Lists all assignments for a given Course, with Edit and Submissions buttons.
 */
public class AssignmentsScreen extends JPanel {
  private final MainWindow mainWindow;
  private final Course course;
  private final ArrayList<Assignment> assignments;

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
    this.assignments = new ArrayList<>(course.getAssignments());

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
    SimpleDateFormat fmt = new SimpleDateFormat("MMM d, yyyy hh:mm a");
    assignments.forEach(a -> {
      JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));

      JLabel nameLabel = new JLabel(a.getName());
      nameLabel.setPreferredSize(new Dimension(130, 20));

      JLabel pointsLabel = new JLabel(a.getPoints() + " pts");
      pointsLabel.setPreferredSize(new Dimension(50, 20));

      JLabel dueDateLabel = new JLabel(fmt.format(a.getDueDate()));
      dueDateLabel.setPreferredSize(new Dimension(150, 20));

      JLabel categoryLabel = new JLabel(a.getCategory().getName());
      categoryLabel.setPreferredSize(new Dimension(75, 20));

      JLabel publishedLabel = new JLabel(a.isPublished() ? "Published" : "Draft");
      publishedLabel.setForeground(a.isPublished() ? new Color(27, 150, 27) : Color.RED);
      publishedLabel.setPreferredSize(new Dimension(63, 20));

      row.add(nameLabel);
      row.add(Box.createHorizontalStrut(5));
      row.add(pointsLabel);
      row.add(Box.createHorizontalStrut(5));
      row.add(dueDateLabel);
      row.add(Box.createHorizontalStrut(5));
      row.add(categoryLabel);
      row.add(Box.createHorizontalStrut(5));
      row.add(publishedLabel);
      row.add(Box.createHorizontalGlue());

      // Submissions button: open SubmissionsScreen
      JButton subs = new JButton("Submissions");
      subs.addActionListener(ev -> mainWindow.getNavigator().push(SubmissionsScreen.getKey(mainWindow, a)));
      row.add(subs);

      // Edit button: open AssignmentEditorPanel
      JButton edit = new JButton("Edit");
      edit.addActionListener(
          ev -> mainWindow.getNavigator().push(AssignmentEditorScreen.getEditKey(mainWindow, course, a)));
      row.add(edit);

      row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

      list.add(row);
    });
    add(new JScrollPane(list), BorderLayout.CENTER);

    // ── Bottom bar ────────────────────────────
    JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton create = new JButton("Create New Assignment");
    create.addActionListener(
        ev -> mainWindow.getNavigator().push(AssignmentEditorScreen.getCreateKey(mainWindow, course)));
    bottom.add(create);
    add(bottom, BorderLayout.SOUTH);
  }
}
