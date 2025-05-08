package views.assignments;

import views.MainWindow;
import views.assignments.submissions.SubmissionsScreen;

import javax.swing.*;

import model.Assignment;
import model.Course;

import java.awt.*;
import java.util.List;

public class GraderAssignmentsScreen extends JPanel {
    private final MainWindow mainWindow;
    private final Course course;

    public static String getKey(MainWindow mainWindow, Course course) {
        String key = "graderAssignments:" + course.getId();
        mainWindow.getNavigator().register(key,
                () -> new GraderAssignmentsScreen(mainWindow, course, () -> mainWindow.getNavigator().back()));
        return key;
    }

    /**
     * @param mainWindow gives us getNavigator()
     * @param course     whose assignments
     * @param onBack     what to do when Back is clicked
     */
    private GraderAssignmentsScreen(MainWindow mainWindow,
            Course course,
            Runnable onBack) {
        this.mainWindow = mainWindow;
        this.course = course;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // ── Top bar ────────────────────────────────
        JPanel top = new JPanel(new BorderLayout());
        JButton back = new JButton("Back");
        back.addActionListener(e -> mainWindow.getNavigator().back());
        top.add(back, BorderLayout.WEST);

        JLabel title = new JLabel("Assignments – " + course.getCode(),
                SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        top.add(title, BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);

        // ── Assignment list ────────────────────────
        List<Assignment> listData = course.getAssignments();
        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        for (Assignment a : listData) {
            JPanel row = new JPanel(new BorderLayout(5, 5));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

            JLabel info = new JLabel(a.getName() + " — " + a.getPoints() + " pts");
            row.add(info, BorderLayout.WEST);

            JButton subs = new JButton("Submissions");
            subs.addActionListener(ev -> mainWindow.getNavigator().push(SubmissionsScreen.getKey(mainWindow, a)));
            row.add(subs, BorderLayout.EAST);

            list.add(row);
            list.add(Box.createVerticalStrut(5));
        }

        add(new JScrollPane(list), BorderLayout.CENTER);
    }
}
