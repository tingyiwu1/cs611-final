// views/RosterPanel.java
package views;

import obj.Grader;
import obj.Student;
import obj.Course;

import javax.swing.*;
import java.awt.*;

public class StudentRosterFrame extends JPanel {
    private final MainWindow mainWindow;
    private final Course course;

    public static String getKey(MainWindow mainWindow, Course course) {
        String key = "roster:" + course.getId();
        mainWindow.getNavigator().register(key,
                () -> new StudentRosterFrame(mainWindow, course));
        return key;
    }

    /**
     * @param mainWindow so you can call navigator.back() if needed
     * @param course     whose roster to display
     * @param onBack     Runnable to pop back to the previous card
     */
    private StudentRosterFrame(MainWindow mainWindow,
            Course course) {
        this.mainWindow = mainWindow;
        this.course = course;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // ── Top bar ────────────────────────────────
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton back = new JButton("Back");
        back.addActionListener(e -> mainWindow.getNavigator().back());
        topBar.add(back);

        JLabel title = new JLabel(course.getCode() + " Roster");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        topBar.add(title);

        add(topBar, BorderLayout.NORTH);

        // ── Split pane: Graders | Students ────────
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.5);

        // Graders
        DefaultListModel<String> gradModel = new DefaultListModel<>();
        for (Grader g : course.getGraders()) {
            gradModel.addElement(g.getName());
        }
        JList<String> gradList = new JList<>(gradModel);
        JPanel left = new JPanel(new BorderLayout());
        left.setBorder(BorderFactory.createTitledBorder("Graders"));
        left.add(new JScrollPane(gradList), BorderLayout.CENTER);
        split.setLeftComponent(left);

        // Students
        DefaultListModel<String> stuModel = new DefaultListModel<>();
        for (Student s : course.getEnrolledStudents()) {
            stuModel.addElement(s.getName());
        }
        JList<String> stuList = new JList<>(stuModel);
        JPanel right = new JPanel(new BorderLayout());
        right.setBorder(BorderFactory.createTitledBorder("Students"));
        right.add(new JScrollPane(stuList), BorderLayout.CENTER);
        split.setRightComponent(right);

        add(split, BorderLayout.CENTER);

        // (optional) you can add “View Details” or other controls in a bottom bar
    }
}
