// Java
package views;

import obj.Course;
import auth.Auth;
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

        // Top bar with Back + Title
        JPanel topPanel = new JPanel(new BorderLayout());
        backButton = new JButton("Back");
        backButton.addActionListener(e -> onBack());
        topPanel.add(backButton, BorderLayout.WEST);

        Course course = mainWindow.getCurrentCourse();
        String title = (course != null)
                ? course.getCode() + " – " + course.getName()
                : "No Course Selected";
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 24f));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // Button grid
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 20, 20));

        // Assignments button
        JButton assignmentsBtn = new JButton("Assignments");
        assignmentsBtn.setEnabled(course != null);
        assignmentsBtn.addActionListener(e -> openAssignments(course));

        // Roster button
        JButton rosterBtn = new JButton("Roster");
        rosterBtn.setEnabled(course != null);
        rosterBtn.addActionListener(e -> openRoster(course));

        // placeholders
        JButton fakeBtn1 = new JButton("UNIMPLEMENTED");
        fakeBtn1.setFont(fakeBtn1.getFont().deriveFont(Font.BOLD, 18f));
        JButton fakeBtn2 = new JButton("UNIMPLEMENTED");
        fakeBtn2.setFont(fakeBtn2.getFont().deriveFont(Font.BOLD, 18f));

        buttonPanel.add(assignmentsBtn);
        buttonPanel.add(rosterBtn);
        buttonPanel.add(fakeBtn1);
        buttonPanel.add(fakeBtn2);

        add(buttonPanel, BorderLayout.CENTER);
    }

    private void onBack() {
        // switch back to the course list card
        mainWindow.switchPanel("courseList");
    }

    private void openAssignments(Course course) {
        if (course == null) return;
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Assignments – " + course.getCode());
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            // top bar with Back
            JPanel main = new JPanel(new BorderLayout(10,10));
            JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton back = new JButton("Back");
            back.addActionListener(e -> frame.dispose());
            top.add(back);
            main.add(top, BorderLayout.NORTH);

            // choose per role
            switch (mainWindow.auth.getUserType()) {
                case INSTRUCTOR:
                    main.add(new AssignmentsScreen(course), BorderLayout.CENTER);
                    break;
                case STUDENT:
                    // your student panel from before
                    Student s = mainWindow.auth.getStudent().orElseThrow();
                    main.add(new StudentAssignmentsPanel(course, s), BorderLayout.CENTER);
                    break;
                case GRADER:
                    main.add(new GraderAssignmentsPanel(course), BorderLayout.CENTER);
                    break;
            }

            frame.getContentPane().add(main);
            frame.pack();
            frame.setLocationRelativeTo(this);
            frame.setVisible(true);
        });
    }



    private void openRoster(Course course) {
        if (course == null) return;
        SwingUtilities.invokeLater(() -> {
            // Assuming StudentRosterFrame is meant to be a standalone window.
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