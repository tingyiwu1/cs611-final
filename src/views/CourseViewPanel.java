package views;

import auth.Auth;
import obj.Course;
import obj.Student;

import javax.swing.*;
import java.awt.*;

public class CourseViewPanel extends JPanel {
    private final MainWindow mainWindow;
    private JButton backButton;

    public CourseViewPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // --- Top bar with Back + Title ---
        JPanel topPanel = new JPanel(new BorderLayout());
        backButton = new JButton("Back");
        backButton.addActionListener(e -> mainWindow.switchPanel("courseList"));
        topPanel.add(backButton, BorderLayout.WEST);

        Course course = mainWindow.getCurrentCourse();
        String title = (course != null)
                ? course.getCode() + " – " + course.getName()
                : "No Course Selected";
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 24f));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // --- Determine role and show/hide Roster ---
        Auth.UserType role = mainWindow.auth.getUserType();
        boolean isInstructor = (role == Auth.UserType.INSTRUCTOR);

        // --- Button grid (2×2) ---
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 20, 20));

        // Assignments (everyone)
        JButton assignmentsBtn = new JButton("Assignments");
        assignmentsBtn.setEnabled(course != null);
        assignmentsBtn.addActionListener(e -> openAssignments(course));
        buttonPanel.add(assignmentsBtn);

        // Roster (instructors only)
        JButton rosterBtn = new JButton("Roster");
        rosterBtn.setEnabled(isInstructor && course != null);
        rosterBtn.setVisible(isInstructor);
        rosterBtn.addActionListener(e -> openRoster(course));
        buttonPanel.add(rosterBtn);

        // placeholders
        JButton fakeBtn1 = new JButton("UNIMPLEMENTED");
        fakeBtn1.setFont(fakeBtn1.getFont().deriveFont(Font.BOLD, 18f));
        JButton gradingBtn = new JButton("Grading");
        gradingBtn.setFont(gradingBtn.getFont().deriveFont(Font.BOLD, 18f));
        gradingBtn.addActionListener(e -> {
            mainWindow.openCourseGrading(course);
        });

        buttonPanel.add(fakeBtn1);
        buttonPanel.add(gradingBtn);

        add(buttonPanel, BorderLayout.CENTER);
    }

    private void openAssignments(Course course) {
        if (course == null) return;
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Assignments – " + course.getCode());
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            // wrap with its own Back
            JPanel wrapper = new JPanel(new BorderLayout(10, 10));
            JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton back = new JButton("Back");
            back.addActionListener(e -> frame.dispose());
            top.add(back);
            wrapper.add(top, BorderLayout.NORTH);

            // choose the right panel per role
            switch (mainWindow.auth.getUserType()) {
                case INSTRUCTOR:
                    wrapper.add(new AssignmentsScreen(course), BorderLayout.CENTER);
                    break;
                case GRADER:
                    wrapper.add(new GraderAssignmentsPanel(course), BorderLayout.CENTER);
                    break;
                case STUDENT:
                default:
                    Student s = mainWindow.auth.getStudent().orElseThrow(
                            () -> new IllegalStateException("Student not found"));
                    wrapper.add(new StudentAssignmentsPanel(course, s), BorderLayout.CENTER);
                    break;
            }

            frame.getContentPane().add(wrapper);
            frame.pack();
            frame.setLocationRelativeTo(this);
            frame.setVisible(true);
        });
    }

    private void openRoster(Course course) {
        if (course == null) return;
        SwingUtilities.invokeLater(() -> {
            StudentRosterFrame rosterFrame = new StudentRosterFrame(
                    course.getGraders(),
                    course.getEnrolledStudents()
            );
            rosterFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            rosterFrame.pack();
            rosterFrame.setLocationRelativeTo(this);
            rosterFrame.setVisible(true);
        });
    }
}
