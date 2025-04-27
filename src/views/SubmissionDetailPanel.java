package views;

import obj.Submission;

import javax.swing.*;
import java.awt.*;

public class SubmissionDetailPanel extends JPanel {
    private final Submission submission;
    private final JTextArea  submissionArea;
    private final JSpinner   gradeSpinner;
    private final JButton    backButton;

    public SubmissionDetailPanel(Submission submission) {
        this.submission = submission;
        setLayout(new BorderLayout(10, 10));

        // Top bar
        JPanel top = new JPanel(new BorderLayout());
        backButton = new JButton("Back");
        backButton.addActionListener(e -> onBack());
        top.add(backButton, BorderLayout.WEST);

        String studentName = submission.getStudent().getName();
        JLabel title = new JLabel("Submission by " + studentName, SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        top.add(title, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        // Submission text
        submissionArea = new JTextArea(10, 30);
        submissionArea.setEditable(false);
        submissionArea.setText(submission.getContent());
        add(new JScrollPane(submissionArea), BorderLayout.CENTER);

        // Grading controls
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(new JLabel("Grade:"));

        int initial = submission.getGrade().orElse(0);  // primitive int
        int maxPts  = submission.getAssignment().getPoints();
        gradeSpinner = new JSpinner(new SpinnerNumberModel(
                initial,    // int
                0,          // int
                maxPts,     // int
                1           // int
        ));
        bottom.add(gradeSpinner);

        JButton saveBtn = new JButton("Save Grade");
        saveBtn.addActionListener(e -> onSave());
        bottom.add(saveBtn);

        add(bottom, BorderLayout.SOUTH);
    }

    private void onBack() {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof JFrame) ((JFrame) w).dispose();
    }

    private void onSave() {
        int grade = (Integer) gradeSpinner.getValue();
        submission.setGrade(grade);
        // If you want, you can also persist:
        submission.getStore().upsert(submission);
        submission.getStore().save();

        JOptionPane.showMessageDialog(
                this,
                "Saved grade " + grade + " for " + submission.getStudent().getName(),
                "Grade Saved",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
