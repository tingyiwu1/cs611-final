// Java
package views;

import obj.Grader;
import obj.Student;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Displays the list of graders and enrolled students for a course.
 */
public class StudentRosterFrame extends JFrame {
    private final DefaultListModel<String> gradersModel = new DefaultListModel<>();
    private final JList<String>            gradersList  = new JList<>(gradersModel);

    private final DefaultListModel<String> studentsModel = new DefaultListModel<>();
    private final JList<String>            studentsList  = new JList<>(studentsModel);

    private final JButton viewDetailsButton = new JButton("View Student Details");

    public StudentRosterFrame(List<Grader> graders, List<Student> students) {
        super("Course Roster");
        setLayout(new BorderLayout(10, 10));

        // Fill graders
        JPanel left = new JPanel(new BorderLayout());
        left.setBorder(BorderFactory.createTitledBorder("Graders"));
        graders.forEach(g -> gradersModel.addElement(g.getName()));
        left.add(new JScrollPane(gradersList), BorderLayout.CENTER);
        JButton addGrader = new JButton("Add Grader");
        addGrader.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Grader name:");
            if (name != null && !name.trim().isEmpty()) gradersModel.addElement(name.trim());
        });
        left.add(addGrader, BorderLayout.SOUTH);

        // Fill students
        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(BorderFactory.createTitledBorder("Students"));
        students.forEach(s -> studentsModel.addElement(s.getName()));
        studentsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        center.add(new JScrollPane(studentsList), BorderLayout.CENTER);

        // Configure view details button
        viewDetailsButton.setEnabled(false);
        viewDetailsButton.addActionListener(e -> showStudentDetails(students));
        studentsList.addListSelectionListener(e ->
                viewDetailsButton.setEnabled(!studentsList.isSelectionEmpty())
        );

        // Bottom panel with back button on the left and view details button on the right
        JPanel bottom = new JPanel(new BorderLayout());
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> dispose());
        bottom.add(backButton, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(viewDetailsButton);
        bottom.add(rightPanel, BorderLayout.EAST);

        // Compose frame
        add(left, BorderLayout.WEST);
        add(center, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void showStudentDetails(List<Student> students) {
        int idx = studentsList.getSelectedIndex();
        if (idx >= 0) {
            Student s = students.get(idx);
            JOptionPane.showMessageDialog(this,
                    "Student ID:   " + s.getId() + "\n" +
                            "Student Name: " + s.getName(),
                    "Details for " + s.getName(),
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
}