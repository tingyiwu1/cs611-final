package views.course;

import auth.Auth;
import obj.Course;
import views.MainWindow;
import views.assignments.AssignmentsScreen;
import views.assignments.GraderAssignmentsPanel;
import views.assignments.StudentAssignmentsPanel;
import views.editcourse.EditCoursePanel;

import javax.swing.*;
import java.awt.*;

public class CourseViewPanel extends JPanel {
    private static final Insets BUTTON_INSETS = new Insets(10, 20, 10, 20);

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

        String title = course.getCode() + " - " + course.getName();
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 24f));
        topBar.add(titleLabel, BorderLayout.CENTER);

        JLabel subTitleLable = new JLabel(course.getTerm().getName());
        subTitleLable.setFont(subTitleLable.getFont().deriveFont(Font.ITALIC, 16f));
        subTitleLable.setHorizontalAlignment(SwingConstants.CENTER);
        topBar.add(subTitleLable, BorderLayout.SOUTH);

        add(topBar, BorderLayout.NORTH);

        // --- Determine role ---
        Auth.UserType role = mainWindow.getAuth().getUserType();
        boolean isInstructor = role == Auth.UserType.INSTRUCTOR;
        boolean isGrader = role == Auth.UserType.GRADER;

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        // content.setBorder(BorderFactory.createCompoundBorder(
        // BorderFactory.createLineBorder(Color.red),
        // content.getBorder()));

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
        assignmentsBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        assignmentsBtn.setMargin(BUTTON_INSETS);
        content.add(assignmentsBtn);

        // 2) Roster (instructors only)
        JButton rosterBtn = new JButton("Roster");
        rosterBtn.setEnabled(isInstructor && course != null);
        rosterBtn.setVisible(isInstructor);
        rosterBtn.addActionListener(
                e -> mainWindow.getNavigator().push(StudentRosterFrame.getKey(mainWindow, course)));
        rosterBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        rosterBtn.setMargin(BUTTON_INSETS);
        content.add(rosterBtn);

        // 3) Grading
        JButton gradingBtn = new JButton("Grading");
        gradingBtn.setEnabled(course != null);
        gradingBtn.setVisible(isInstructor || isGrader);
        gradingBtn.addActionListener(
                e -> mainWindow.getNavigator().push(GradingPanel.getKey(mainWindow, course)));
        gradingBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        gradingBtn.setMargin(BUTTON_INSETS);
        content.add(gradingBtn);

        // 4) Edit button (instructors only)
        JButton editBtn = new JButton("Edit");
        editBtn.setEnabled(isInstructor && course != null);
        editBtn.setVisible(isInstructor);
        editBtn.addActionListener(
                e -> mainWindow.getNavigator().push(EditCoursePanel.getEditKey(mainWindow, course)));
        editBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        editBtn.setMargin(BUTTON_INSETS);
        content.add(editBtn);

        // 5) Clone course (instructors only)
        JButton cloneBtn = new JButton("Clone Course");
        cloneBtn.setEnabled(isInstructor && course != null);
        cloneBtn.setVisible(isInstructor);
        cloneBtn.addActionListener(
                e -> mainWindow.getNavigator().push(EditCoursePanel.getCloneKey(mainWindow, course)));
        cloneBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        cloneBtn.setMargin(BUTTON_INSETS);
        content.add(cloneBtn);

        add(content, BorderLayout.CENTER);
    }
}
