package views;

import auth.Auth;
import obj.Course;
import views.editcourse.EditCoursePanel;

import javax.swing.*;
import java.awt.*;

public class CourseViewPanel extends JPanel {
    private final MainWindow mainWindow;
    private final Course course;

    public static String getKey(MainWindow mainWindow, Course course) {
        String key = "courseView:" + course.getId();
        mainWindow.getNavigator().register(key, () -> new CourseViewPanel(mainWindow, course));
        return key;
    }

    public CourseViewPanel(MainWindow mainWindow, Course course) {
        this.mainWindow = mainWindow;
        this.course = course;
        initComponents();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        // Whenever this panel is shown, re-build to pick up any state changes
        removeAll();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // --- Top bar: Back + Title ---
        JPanel topBar = new JPanel(new BorderLayout());
        JButton back = new JButton("Back");
        back.addActionListener(e -> mainWindow.getNavigator().back());
        topBar.add(back, BorderLayout.WEST);

        String title = (course != null)
                ? course.getCode() + " – " + course.getName()
                : "No Course Selected";
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 24f));
        topBar.add(titleLabel, BorderLayout.CENTER);

        add(topBar, BorderLayout.NORTH);

        // --- Determine role ---
        Auth.UserType role = mainWindow.getAuth().getUserType();
        boolean isInstructor = role == Auth.UserType.INSTRUCTOR;

        JPanel grid = new JPanel();
        grid.setLayout(new BoxLayout(grid, BoxLayout.Y_AXIS));
        // --- Button grid (2×2) ---
        // JPanel grid = new JPanel(new GridLayout(2, 2, 20, 20));
        // grid.setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1) Assignments
        JButton assignmentsBtn = new JButton("Assignments");
        assignmentsBtn.setEnabled(course != null);
        assignmentsBtn.addActionListener(e -> {
            if (isInstructor) {
                mainWindow.getNavigator().push(AssignmentsScreen.getKey(mainWindow, course));
            } else if (role == Auth.UserType.GRADER) {
                mainWindow.getNavigator().push(GraderAssignmentsPanel.getKey(mainWindow, course));
            } else {
                mainWindow.getNavigator().push(StudentAssignmentsPanel.getKey(mainWindow, course));
            }
        });
        grid.add(assignmentsBtn);

        // 2) Roster (instructors only)
        JButton rosterBtn = new JButton("Roster");
        rosterBtn.setEnabled(isInstructor && course != null);
        rosterBtn.setVisible(isInstructor);
        rosterBtn.addActionListener(
                e -> mainWindow.getNavigator().push(StudentRosterFrame.getKey(mainWindow, course)));
        grid.add(rosterBtn);

        // 3) Edit button (instructors only)
        JButton editBtn = new JButton("Edit");
        editBtn.setEnabled(isInstructor && course != null);
        editBtn.setVisible(isInstructor);
        editBtn.addActionListener(
                e -> mainWindow.getNavigator().push(EditCoursePanel.getEditKey(mainWindow, course)));
        grid.add(editBtn);

        // 4) Grading
        JButton gradingBtn = new JButton("Grading");
        gradingBtn.setEnabled(course != null);
        gradingBtn.addActionListener(
                e -> mainWindow.getNavigator().push(GradingPanel.getKey(mainWindow, course)));
        grid.add(gradingBtn);

        add(grid, BorderLayout.CENTER);
    }
}
