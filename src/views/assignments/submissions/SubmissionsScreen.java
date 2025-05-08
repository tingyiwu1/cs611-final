package views.assignments.submissions;

import views.MainWindow;

import javax.swing.*;

import model.Assignment;
import model.Submission;

import java.awt.*;

/**
 * Displays submissions for a given assignment.
 * Integrated into Navigator stack; uses onBack callback.
 */
public class SubmissionsScreen extends JPanel {
    private final MainWindow mainWindow;
    private final Assignment assignment;

    private final DefaultListModel<Submission> submissionsModel;
    private final JList<Submission> submissionsList;

    public static String getKey(MainWindow mainWindow, Assignment assignment) {
        String key = "submissions:" + assignment.getId();
        mainWindow.getNavigator().register(key, () -> new SubmissionsScreen(mainWindow, assignment));
        return key;
    }

    private SubmissionsScreen(MainWindow mainWindow, Assignment assignment) {
        this.mainWindow = mainWindow;
        this.assignment = assignment;
        this.submissionsModel = new DefaultListModel<>();
        this.submissionsList = new JList<>(submissionsModel);
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
                "Submissions - " + assignment.getName(),
                SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        assignment.getSubmissions().forEach(submissionsModel::addElement);

        submissionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.submissionsList.setCellRenderer(new ListCellRenderer<Submission>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends Submission> list, Submission value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                String score = value.getGrade().map(g -> g + "/" + value.getAssignment().getPoints())
                        .orElse("Not graded");
                JLabel label = new JLabel(
                        value.getStudent().getName() + " (score: " + score + ")");
                label.setOpaque(true);
                if (isSelected) {
                    label.setBackground(list.getSelectionBackground());
                    label.setForeground(list.getSelectionForeground());
                } else {
                    label.setBackground(list.getBackground());
                    label.setForeground(list.getForeground());
                }
                return label;
            }
        });

        add(new JScrollPane(submissionsList), BorderLayout.CENTER);

        // Bottom: View Submission button
        JButton viewButton = new JButton("View Submission");
        viewButton.addActionListener(e -> mainWindow.getNavigator()
                .push(SubmissionDetailPanel.getKey(mainWindow, submissionsList.getSelectedValue())));
        viewButton.setEnabled(false);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        submissionsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                viewButton.setEnabled(submissionsList.getSelectedValue() != null);
            }
        });

        buttonPanel.add(viewButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}