package views;

import obj.Submission;
import javax.swing.*;
import java.awt.*;

/**
 * Displays the content of a single Submission and allows grading.
 * Integrated into Navigator stack; uses onBack callback.
 */
public class SubmissionDetailPanel extends JPanel {
    private final MainWindow mainWindow;
    private final Submission submission;
    private final JTextArea submissionArea;
    private final JSpinner gradeSpinner;

    public static String getKey(MainWindow mainWindow, Submission submission) {
        String key = "submission:" + submission.getId();
        mainWindow.getNavigator().register(key, () -> new SubmissionDetailPanel(mainWindow, submission));
        return key;
    }

    /**
     * @param mainWindow for navigation callbacks
     * @param submission the submission to display
     * @param onBack     pop back to previous screen
     */
    private SubmissionDetailPanel(MainWindow mainWindow, Submission submission) {
        this.mainWindow = mainWindow;
        this.submission = submission;

        setLayout(new BorderLayout(10, 10));

        // Top bar with Back Button and title
        JPanel top = new JPanel(new BorderLayout());
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> mainWindow.getNavigator().back());
        top.add(backButton, BorderLayout.WEST);

        String studentName = submission.getStudent().getName();
        JLabel title = new JLabel("Submission by " + studentName,
                SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        top.add(title, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        // Submission content
        submissionArea = new JTextArea(10, 30);
        submissionArea.setEditable(false);
        submissionArea.setText(submission.getContent());
        add(new JScrollPane(submissionArea), BorderLayout.CENTER);

        // Grading controls
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(new JLabel("Grade:"));

        int initial = submission.getGrade().orElse(0);
        int maxPts = submission.getAssignment().getPoints();
        gradeSpinner = new JSpinner(new SpinnerNumberModel(
                initial,
                0,
                maxPts,
                1));
        bottom.add(gradeSpinner);

        JButton saveBtn = new JButton("Save Grade");
        saveBtn.addActionListener(e -> onSave());
        bottom.add(saveBtn);

        add(bottom, BorderLayout.SOUTH);
    }

    private void onSave() {
        int grade = (Integer) gradeSpinner.getValue();
        submission.setGrade(grade);
        submission.getStore().upsert(submission);
        submission.getStore().save();

        JOptionPane.showMessageDialog(
                this,
                "Saved grade " + grade + " for " + submission.getStudent().getName(),
                "Grade Saved",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
