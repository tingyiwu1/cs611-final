// views/StudentRosterFrame.java
package views;

import obj.Grader;
import obj.Student;
import obj.Course;
import obj.Enrollment;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class StudentRosterFrame extends JPanel {
    private final MainWindow mainWindow;
    private final Course course;

    private DefaultListModel<Student> stuModel;

    public static String getKey(MainWindow mainWindow, Course course) {
        String key = "roster:" + course.getId();
        mainWindow.getNavigator().register(key,
                () -> new StudentRosterFrame(mainWindow, course));
        return key;
    }

    private StudentRosterFrame(MainWindow mainWindow, Course course) {
        this.mainWindow = mainWindow;
        this.course     = course;
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
        stuModel = new DefaultListModel<>();
        for (Student s : course.getEnrolledStudents()) {
            stuModel.addElement(s);
        }
        JList<Student> stuList = new JList<>(stuModel);
        stuList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                                                          Object value,
                                                          int index,
                                                          boolean isSelected,
                                                          boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Student) {
                    setText(((Student) value).getName());
                }
                return this;
            }
        });
        JPanel right = new JPanel(new BorderLayout());
        right.setBorder(BorderFactory.createTitledBorder("Students"));
        right.add(new JScrollPane(stuList), BorderLayout.CENTER);
        split.setRightComponent(right);

        add(split, BorderLayout.CENTER);

        // ── Bottom bar: Add / Remove ───────────────
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Add Student
        JButton addBtn = new JButton("Add Student");
        addBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(
                    this,
                    "Enter Student ID to enroll:",
                    "Add Student",
                    JOptionPane.PLAIN_MESSAGE
            );
            if (input == null) return;
            String studentId = input.trim();
            if (studentId.length() == 0) return;

            Optional<Student> opt = mainWindow
                    .getStore()
                    .get(Student.class, studentId);
            if (!opt.isPresent()) {
                JOptionPane.showMessageDialog(
                        this,
                        "No student found with ID: " + studentId,
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            Student student = opt.get();
            course.enrollStudent(student);
            mainWindow.getStore().save();

            if (!stuModel.contains(student)) {
                stuModel.addElement(student);
            }
        });
        bottomBar.add(addBtn);

        // Remove Student
        JButton removeBtn = new JButton("Remove Student");
            removeBtn.addActionListener(e -> {
                Student selected = stuList.getSelectedValue();
                if (selected == null) { /* warn and return */ }

                int choice = JOptionPane.showConfirmDialog(
                        this,
                        "Drop " + selected.getName() + " from this course?",
                        "Confirm Removal",
                        JOptionPane.YES_NO_OPTION
                );
                if (choice != JOptionPane.YES_OPTION) return;

                selected.dropEnrollment(course);
                mainWindow.getStore().save();
                stuModel.removeElement(selected);
            });
        bottomBar.add(removeBtn);

        add(bottomBar, BorderLayout.SOUTH);
    }
}
