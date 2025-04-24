package views;

import grading.GradeCalculator;
import testclasses.testAssignment;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class CourseOverviewView extends JFrame {
    private JButton btnSetGradingRule;
    private JButton btnBeginGrading;
    private JList<String> studentList;
    private DefaultListModel<String> studentListModel;
    private JLabel courseLabel;
    private GradeCalculator calculator;

    public CourseOverviewView(GradeCalculator calculator, String courseName, List<String> enrolledStudents) {
        this.calculator = calculator;
        
        setTitle("Course Overview");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        courseLabel = new JLabel("Course: " + courseName);
        courseLabel.setFont(new Font("Arial", Font.BOLD, 20));
        courseLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(courseLabel, BorderLayout.NORTH);

        studentListModel = new DefaultListModel<>();
        for (String student : enrolledStudents) {
            studentListModel.addElement(student);
        }
        studentList = new JList<>(studentListModel);
        add(new JScrollPane(studentList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnSetGradingRule = new JButton("Set Grading Rule");
        btnBeginGrading = new JButton("Begin Grading");
        buttonPanel.add(btnSetGradingRule);
        buttonPanel.add(btnBeginGrading);
        add(buttonPanel, BorderLayout.SOUTH);

        btnSetGradingRule.addActionListener(e -> {
            Map<String, Double> currentWeights = calculator.getAssignmentWeights();  // ← 新增 getter
            SetGradingRuleView ruleView = new SetGradingRuleView(currentWeights);
            ruleView.setGradeCalculator(calculator);
            ruleView.setParentView(this);
            ruleView.setVisible(true);
            setVisible(false);
        });


        btnBeginGrading.addActionListener(e -> {
            GradeStatisticsView statView = new GradeStatisticsView();
            statView.setGradeCalculator(calculator);
            statView.setVisible(true);
            setVisible(false);
        });
    }

    public void setGradeCalculator(GradeCalculator calculator) {
        this.calculator = calculator;
    }
}
