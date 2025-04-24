package views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class StudentRosterFrame extends JFrame {
    private DefaultListModel<String> gradersModel;
    private JList<String> gradersList;
    private JButton addGraderButton;

    private DefaultListModel<Student> studentsModel;
    private JList<Student> studentsList;

    private JLabel gradeLabel;
    private JButton viewDetailsButton;

    public StudentRosterFrame(java.util.List<String> graders, java.util.List<Student> students) {
        super("Student Roster");

        // Graders panel
        gradersModel = new DefaultListModel<>();
        graders.forEach(gradersModel::addElement);
        gradersList = new JList<>(gradersModel);
        addGraderButton = new JButton("Add Grader");
        addGraderButton.addActionListener(e -> addGrader());

        // Students panel
        studentsModel = new DefaultListModel<>();
        students.forEach(studentsModel::addElement);
        studentsList = new JList<>(studentsModel);
        studentsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentsList.addListSelectionListener(e -> updateGradeInfo());

        // Detail panel
        gradeLabel = new JLabel("Select a student to view grade");
        viewDetailsButton = new JButton("View Details");
        viewDetailsButton.addActionListener(e -> viewStudentDetails());
        viewDetailsButton.setEnabled(false);

        // Layout setup
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Graders"));
        leftPanel.add(new JScrollPane(gradersList), BorderLayout.CENTER);
        leftPanel.add(addGraderButton, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Students"));
        centerPanel.add(new JScrollPane(studentsList), BorderLayout.CENTER);

        JPanel detailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        detailPanel.add(gradeLabel);
        detailPanel.add(viewDetailsButton);

        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().add(leftPanel, BorderLayout.WEST);
        getContentPane().add(centerPanel, BorderLayout.CENTER);
        getContentPane().add(detailPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addGrader() {
        String name = JOptionPane.showInputDialog(this, "Enter grader name:");
        if (name != null && !name.trim().isEmpty()) {
            gradersModel.addElement(name.trim());
        }
    }

    private void updateGradeInfo() {
        Student s = studentsList.getSelectedValue();
        if (s != null) {
            gradeLabel.setText(String.format("Weighted: %.2f%% (%s)", s.getWeightedPercentage(), s.getLetterGrade()));
            viewDetailsButton.setEnabled(true);
        } else {
            gradeLabel.setText("Select a student to view grade");
            viewDetailsButton.setEnabled(false);
        }
    }

    private void viewStudentDetails() {
        Student s = studentsList.getSelectedValue();
        if (s != null) {
            new StudentDetailFrame(s);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            java.util.List<String> graders = Arrays.asList("Alice", "Bob");
            java.util.List<Student> students = Arrays.asList(
                new Student("John Doe", 85.5),
                new Student("Jane Smith", 92.0),
                new Student("Mike Brown", 76.3)
            );
            new StudentRosterFrame(graders, students);
        });
    }
}

class Student {
    private String name;
    private double weightedPercentage;

    public Student(String name, double weightedPercentage) {
        this.name = name;
        this.weightedPercentage = weightedPercentage;
    }

    public String getName() { return name; }
    public double getWeightedPercentage() { return weightedPercentage; }

    public String getLetterGrade() {
        double p = weightedPercentage;
        if (p >= 90) return "A";
        if (p >= 80) return "B";
        if (p >= 70) return "C";
        if (p >= 60) return "D";
        return "F";
    }

    @Override
    public String toString() {
        return name;
    }
}

class StudentDetailFrame extends JFrame {
    public StudentDetailFrame(Student student) {
        super("Details - " + student.getName());
        JTextArea textArea = new JTextArea(10, 30);
        textArea.setEditable(false);
        textArea.setText(String.format(
            "Name: %s%nWeighted Percentage: %.2f%%%nLetter Grade: %s%n",
            student.getName(), student.getWeightedPercentage(), student.getLetterGrade()
        ));
        add(new JScrollPane(textArea));
        setSize(400, 200);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
