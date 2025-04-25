package views;

import obj.Assignment;
import obj.Submission;

import javax.swing.*;
import java.awt.*;

public class SubmissionsScreen extends JPanel {
    private final Assignment assignment;
    private final DefaultListModel<String> studentsModel;
    private final JList<String>            studentsList;
    private JButton                         backButton;

    public SubmissionsScreen(Assignment assignment) {
        this.assignment = assignment;
        this.studentsModel = new DefaultListModel<>();
        this.studentsList  = new JList<>(studentsModel);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Top bar
        JPanel topPanel = new JPanel(new BorderLayout());
        backButton = new JButton("Back");
        backButton.addActionListener(e -> onBack());
        topPanel.add(backButton, BorderLayout.WEST);

        JLabel titleLabel = new JLabel(
                "Submissions – " + assignment.getId(),
                SwingConstants.CENTER
        );
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // Populate the list with student IDs
        for (Submission sub : assignment.getSubmissions()) {
            studentsModel.addElement(sub.getStudent().getId());
        }

        studentsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(studentsList), BorderLayout.CENTER);

        // Bottom: View Submission button
        JButton viewButton = new JButton("View Submission");
        viewButton.addActionListener(e -> {
            String selectedId = studentsList.getSelectedValue();
            openDetail(selectedId);
        });
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(viewButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void onBack() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof JFrame) {
            ((JFrame) window).dispose();
        }
    }

    private void openDetail(String studentId) {
        if (studentId == null) return;

        // find the Submission object for this student
        Submission found = null;
        for (Submission sub : assignment.getSubmissions()) {
            if (sub.getStudent().getId().equals(studentId)) {
                found = sub;
                break;
            }
        }
        if (found == null) return;

        // open the detail panel with the single-arg constructor
        Submission sub = found;
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Submission – " + sub.getStudent().getName());
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.getContentPane().add(new SubmissionDetailPanel(sub));
            frame.pack();
            frame.setLocationRelativeTo(this);
            frame.setVisible(true);
        });
    }
}
