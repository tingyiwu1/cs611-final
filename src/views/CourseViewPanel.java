package views;

import auth.Auth;
import obj.Course;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CourseViewPanel extends JPanel {
    private final MainWindow mainWindow;

    public CourseViewPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
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

        Course course = mainWindow.getCurrentCourse();
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

        // --- Button grid (2×2) ---
        JPanel grid = new JPanel(new GridLayout(2, 2, 20, 20));
        grid.setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1) Assignments
        JButton assignmentsBtn = new JButton("Assignments");
        assignmentsBtn.setEnabled(course != null);
        assignmentsBtn.addActionListener(e -> {
            if (course != null) {
                mainWindow.getNavigator().push("assignments");
            }
        });
;
        grid.add(assignmentsBtn);

        // 2) Roster (instructors only)
        JButton rosterBtn = new JButton("Roster");
        rosterBtn.setEnabled(isInstructor && course != null);
        rosterBtn.setVisible(isInstructor);
        rosterBtn.addActionListener(e -> {
            if (course != null) {
                mainWindow.setCurrentCourse(course);
                mainWindow.getNavigator().push("roster");
            }
        });
        grid.add(rosterBtn);

        // 3) Placeholder / UNIMPLEMENTED
        JButton placeholder = new JButton("UNIMPLEMENTED");
        placeholder.setFont(placeholder.getFont().deriveFont(Font.BOLD, 18f));
        grid.add(placeholder);

        // 4) Grading
        JButton gradingBtn = new JButton("Grading");
        gradingBtn.setFont(gradingBtn.getFont().deriveFont(Font.BOLD, 18f));
        gradingBtn.setEnabled(course != null);
        gradingBtn.addActionListener(e -> {
            if (course != null) {
                mainWindow.setCurrentCourse(course);
                mainWindow.getNavigator().push("grading");
            }
        });
        grid.add(gradingBtn);

        add(grid, BorderLayout.CENTER);
    }
}
