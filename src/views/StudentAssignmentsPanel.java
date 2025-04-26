// StudentAssignmentsPanel.java
package views;

import obj.Assignment;
import obj.Course;
import obj.Enrollment;
import obj.Student;
import obj.Submission;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.UUID;

/**
 * A student‐centric view of a course’s assignments.
 * Shows name, category, points, submission status, grade, and a text area + button.
 */
public class StudentAssignmentsPanel extends JPanel {
    private final Course  course;
    private final Student student;
    private final Enrollment enrollment;

    public StudentAssignmentsPanel(Course course, Student student) {
        this.course     = course;
        this.student    = student;
        this.enrollment = course.getEnrollment(student)
                .orElseThrow(() -> new IllegalStateException("Not enrolled"));
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10,10));

        JLabel title = new JLabel("Your Assignments: " + course.getCode(), SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        add(title, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        List<Assignment> assignments = course.getAssignments();
        for (Assignment a : assignments) {
            list.add(new AssignmentRow(a));
            list.add(Box.createVerticalStrut(5));
        }

        add(new JScrollPane(list), BorderLayout.CENTER);
    }

    private class AssignmentRow extends JPanel {
        private final Assignment assignment;
        private Submission submission;

        private final JLabel   statusLabel;
        private final JLabel   gradeLabel;
        private final JTextArea submissionArea;
        private final JButton  submitBtn;

        AssignmentRow(Assignment a) {
            this.assignment = a;
            // find existing submission
            this.submission = a.getSubmissions().stream()
                    .filter(s -> s.getStudent().getId().equals(student.getId()))
                    .findFirst().orElse(null);

            setLayout(new BorderLayout(10,10));
            setBorder(BorderFactory.createLineBorder(Color.GRAY));

            // West: basic info
            JPanel info = new JPanel(new GridLayout(1,3));
            info.add(new JLabel(a.getName()));
            info.add(new JLabel(a.getCategory().getName()));
            info.add(new JLabel(a.getPoints() + " pts"));
            add(info, BorderLayout.WEST);

            // Center: status, grade, text area
            JPanel center = new JPanel(new BorderLayout(5,5));
            boolean done = (submission != null);
            statusLabel = new JLabel(done ? "Submitted" : "Not submitted");
            gradeLabel  = new JLabel(
                    done && submission.getGrade().isPresent()
                            ? "Grade: " + submission.getGrade().get()
                            : "Grade: –"
            );
            JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
            statusBar.add(statusLabel);
            statusBar.add(Box.createHorizontalStrut(20));
            statusBar.add(gradeLabel);
            center.add(statusBar, BorderLayout.NORTH);

            submissionArea = new JTextArea(3, 20);
            if (done) submissionArea.setText(submission.getContent());
            center.add(new JScrollPane(submissionArea), BorderLayout.CENTER);

            add(center, BorderLayout.CENTER);

            // East: submit/update button
            submitBtn = new JButton(done ? "Update" : "Submit");
            submitBtn.addActionListener(e -> onSubmit());
            add(submitBtn, BorderLayout.EAST);
        }

        private void onSubmit() {
            String text = submissionArea.getText().trim();
            if (submission == null) {
                // new submission
                submission = enrollment.createSubmission(assignment, text);
            } else {
                // update existing
                submission.setContent(text);
            }
            // persist
            submission.getStore().upsert(submission);
            submission.getStore().save();

            // refresh labels
            statusLabel.setText("Submitted");
            gradeLabel.setText(
                    submission.getGrade().map(g -> "Grade: " + g).orElse("Grade: –")
            );
            submitBtn.setText("Update");
        }
    }
}
