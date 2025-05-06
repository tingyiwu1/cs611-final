package views;

import obj.Student;
import obj.Course;
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

        // Top bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton back = new JButton("Back");
        back.addActionListener(e -> mainWindow.getNavigator().back());
        topBar.add(back);

        JLabel title = new JLabel(course.getCode() + " Roster");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        topBar.add(title);

        add(topBar, BorderLayout.NORTH);

        // Students list panel
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
        JPanel studentPanel = new JPanel(new BorderLayout());
        studentPanel.setBorder(BorderFactory.createTitledBorder("Students"));
        studentPanel.add(new JScrollPane(stuList), BorderLayout.CENTER);
        add(studentPanel, BorderLayout.CENTER);

        // Bottom bar: Add / Remove Student
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Add Student Button
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

        // Remove Student Button
        JButton removeBtn = new JButton("Remove Student");
        removeBtn.addActionListener(e -> {
            Student selected = stuList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please select a student to remove.",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

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