package views;

import obj.Assignment;
import obj.Course;
import obj.Enrollment;
import obj.Student;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StudentAssignmentsPanel extends JPanel {
    private final MainWindow mainWindow;
    private final Course     course;
    private final Student    student;
    private final Enrollment enrollment;
    private final Runnable   onBack;

    /**
     * @param mainWindow gives us getNavigator()
     * @param course     which course
     * @param student    which student
     * @param onBack     what to do when Back is clicked
     */
    public StudentAssignmentsPanel(MainWindow mainWindow,
                                   Course course,
                                   Student student,
                                   Runnable onBack) {
        this.mainWindow = mainWindow;
        this.course     = course;
        this.student    = student;
        this.enrollment = course.getEnrollment(student)
                .orElseThrow(() -> new IllegalStateException("Not enrolled"));
        this.onBack     = onBack;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10,10));

        // ── Top bar ────────────────────────────────
        JPanel top = new JPanel(new BorderLayout());
        JButton back = new JButton("Back");
        back.addActionListener(e -> onBack.run());
        top.add(back, BorderLayout.WEST);

        JLabel title = new JLabel("Your Assignments – " + course.getCode(),
                SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        top.add(title, BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);

        // ── Assignment rows ────────────────────────
        List<Assignment> assignments = course.getAssignments();
        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

        for (Assignment a : assignments) {
            list.add(new AssignmentRow(a));
            list.add(Box.createVerticalStrut(5));
        }

        add(new JScrollPane(list), BorderLayout.CENTER);
    }

    private class AssignmentRow extends JPanel {
        private obj.Submission submission;
        private final JLabel   statusLabel;
        private final JLabel   gradeLabel;
        private final JTextArea submissionArea;
        private final JButton  submitBtn;

        AssignmentRow(Assignment a) {
            setLayout(new BorderLayout(10,10));
            setBorder(BorderFactory.createLineBorder(Color.GRAY));

            // find existing submission
            submission = a.getSubmissions().stream()
                    .filter(s -> s.getStudent().getId().equals(student.getId()))
                    .findFirst().orElse(null);

            // West: info
            JPanel info = new JPanel(new GridLayout(1,3));
            info.add(new JLabel(a.getName()));
            info.add(new JLabel(a.getCategory().getName()));
            info.add(new JLabel(a.getPoints() + " pts"));
            add(info, BorderLayout.WEST);

            // Center: status + text
            JPanel center = new JPanel(new BorderLayout(5,5));
            boolean done = (submission != null);
            statusLabel = new JLabel(done ? "Submitted" : "Not submitted");
            gradeLabel  = new JLabel(
                    done && submission.getGrade().isPresent()
                            ? "Grade: " + submission.getGrade().get()
                            : "Grade: –"
            );
            JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));
            bar.add(statusLabel);
            bar.add(Box.createHorizontalStrut(20));
            bar.add(gradeLabel);
            center.add(bar, BorderLayout.NORTH);

            submissionArea = new JTextArea(3,20);
            if (done) submissionArea.setText(submission.getContent());
            center.add(new JScrollPane(submissionArea), BorderLayout.CENTER);

            add(center, BorderLayout.CENTER);

            // East: submit/update & view submissions
            JPanel east = new JPanel(new GridLayout(2,1,5,5));

            JButton viewSubs = new JButton("Submissions");
            viewSubs.addActionListener(e -> {
                String key = "submissions:" + a.getId();
                mainWindow.getNavigator().register(
                        key,
                        () -> new SubmissionsScreen(mainWindow, a, onBack)
                );
                mainWindow.getNavigator().push(key);
            });
            east.add(viewSubs);

            submitBtn = new JButton(done ? "Update" : "Submit");
            submitBtn.addActionListener(e -> onSubmit(a));
            east.add(submitBtn);

            add(east, BorderLayout.EAST);
        }

        private void onSubmit(Assignment a) {
            String text = submissionArea.getText().trim();
            if (submission == null) {
                submission = enrollment.createSubmission(a, text);
            } else {
                submission.setContent(text);
            }
            submission.getStore().upsert(submission);
            submission.getStore().save();

            // refresh UI
            statusLabel.setText("Submitted");
            gradeLabel.setText(
                    submission.getGrade()
                            .map(g -> "Grade: " + g)
                            .orElse("Grade: –")
            );
            submitBtn.setText("Update");
        }
    }
}
