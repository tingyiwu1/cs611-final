package views;

import grading.GradeCalculator;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import obj.Assignment;
import obj.Submission;

public class CourseViewPanel extends JPanel {
    private MainWindow mainWindow;
    private JLabel titleLabel;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnSetGradingRule;
    private JButton btnBeginGrading;
    private List<String> studentIds;
    private List<String> assignmentIds;
    private GradeCalculator calculator;

    public CourseViewPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.calculator = mainWindow.getCurrentCalculator(); 

        this.studentIds = calculator.getCourse().getEnrolledStudents().stream()
                .map(s -> s.getId()).collect(Collectors.toList());

        this.assignmentIds = calculator.getAssignments().stream()
                .map(Assignment::getId).collect(Collectors.toList());

        setLayout(new BorderLayout());

        titleLabel = new JLabel(
            "Course: " + calculator.getCourse().getCode() +
            " | Semester: " + calculator.getCourse().getTerm().getSeason() + " " + calculator.getCourse().getTerm().getYear(),
            SwingConstants.CENTER
        );
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        tableModel = buildTableModel();
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnSetGradingRule = new JButton("Set Grading Rule");
        btnBeginGrading = new JButton("Begin Grading");
        buttonPanel.add(btnSetGradingRule);
        buttonPanel.add(btnBeginGrading);
        add(buttonPanel, BorderLayout.SOUTH);

        btnSetGradingRule.addActionListener(e -> {
            SetGradingRulePanel rulePanel = new SetGradingRulePanel(calculator.getAssignmentWeights());
            rulePanel.setGradeCalculator(calculator);
            rulePanel.setOnSaveCallback(() -> mainWindow.switchPanel("courseView"));
            mainWindow.getLoggedInPanel().add(rulePanel, "setGradingRule");
            mainWindow.switchPanel("setGradingRule");
        });

        btnBeginGrading.addActionListener(e -> {
            updateFinalScores();
            GradeStatisticsPanel statPanel = new GradeStatisticsPanel(mainWindow);
            statPanel.setGradeCalculator(calculator);
            mainWindow.getLoggedInPanel().add(statPanel, "statistics");
            mainWindow.switchPanel("statistics");
        });
    }

    private DefaultTableModel buildTableModel() {
        String[] columnNames = buildColumnHeaders();
        Object[][] rowData = buildTableData();
        return new DefaultTableModel(rowData, columnNames) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private String[] buildColumnHeaders() {
        List<String> columns = new ArrayList<>();
        columns.add("Student");
        columns.add("Submitted");
        columns.addAll(assignmentIds);
        columns.add("Final Score");
        return columns.toArray(new String[0]);
    }

    private Object[][] buildTableData() {
        List<Assignment> assignments = calculator.getAssignments();
        Map<String, Set<String>> studentSubmissions = new HashMap<>();
        for (Assignment a : assignments) {
            for (Submission s : a.getSubmissions()) {
                studentSubmissions
                        .computeIfAbsent(s.getStudent().getId(), k -> new HashSet<>())
                        .add(a.getId());
            }
        }

        Object[][] data = new Object[studentIds.size()][2 + assignmentIds.size() + 1];
        for (int i = 0; i < studentIds.size(); i++) {
            String sid = studentIds.get(i);
            Set<String> submitted = studentSubmissions.getOrDefault(sid, new HashSet<>());

            data[i][0] = sid;
            data[i][1] = submitted.size() + "/" + assignmentIds.size();
            for (int j = 0; j < assignmentIds.size(); j++) {
                String aid = assignmentIds.get(j);
                data[i][2 + j] = submitted.contains(aid) ? "✓" : "";
            }
            data[i][2 + assignmentIds.size()] = "";
        }

        return data;
    }

    public void updateFinalScores() {
        Map<String, Double> scores = calculator.calculateAllStudentGrades();
        for (int row = 0; row < studentIds.size(); row++) {
            String sid = (String) tableModel.getValueAt(row, 0);
            Double score = scores.get(sid);
            tableModel.setValueAt(String.format("%.2f", score), row, 2 + assignmentIds.size());
        }
    }
}
