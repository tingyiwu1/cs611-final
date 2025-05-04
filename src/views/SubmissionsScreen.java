package views;

import obj.Assignment;
import obj.Submission;
import store.Store;

import javax.swing.*;
import java.awt.*;

/**
 * Displays submissions for a given assignment.
 * Integrated into Navigator stack; uses onBack callback.
 */
public class SubmissionsScreen extends JPanel {
    private final MainWindow mainWindow;
    private final Assignment assignment;
    private final DefaultListModel<String> studentsModel;
    private final JList<String> studentsList;

    public static String getKey(MainWindow mainWindow, Assignment assignment) {
        String key = "submissions:" + assignment.getId();
        mainWindow.getNavigator().register(key, () -> new SubmissionsScreen(mainWindow, assignment));
        return key;
    }

    private SubmissionsScreen(MainWindow mainWindow, Assignment assignment) {
        this.mainWindow = mainWindow;
        this.assignment = assignment;
        this.studentsModel = new DefaultListModel<>();
        this.studentsList = new JList<>(studentsModel);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Top bar
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> mainWindow.getNavigator().back());
        topPanel.add(backButton, BorderLayout.WEST);

        JLabel titleLabel = new JLabel(
                "Submissions – " + assignment.getName(),
                SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // Populate the list with student IDs
        for (Submission sub : assignment.getSubmissions()) {
            String studentId = sub.getStudent().getId();
    String scoreStr = sub.getGrade().map(Object::toString).orElse("Not graded");
    studentsModel.addElement(studentId + " (score: " + scoreStr + ")");
            //studentsModel.addElement(sub.getStudent().getId());
        }

        studentsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(studentsList), BorderLayout.CENTER);

        // Bottom: View Submission button
        JButton viewButton = new JButton("View Submission");
        viewButton.addActionListener(e -> openDetail(studentsList.getSelectedValue()));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(viewButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void openDetail(String studentId) {
        if (studentId == null)
            return;
        Submission found = assignment.getSubmissions().stream()
                .filter(s -> s.getStudent().getId().equals(studentId))
                .findFirst().orElse(null);
        if (found == null)
            return;
        mainWindow.getNavigator().push(SubmissionDetailPanel.getKey(mainWindow, found));
    }
}