package views;

import grading.GradeCalculator;
import obj.Assignment;
import obj.Submission;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Panel for instructors to view and grade student submissions.
 */
public class GradingPanel extends JPanel {
    private final MainWindow mainWindow;
    private final GradeCalculator calculator;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<String> studentIds;
    private List<String> assignmentIds;

    public GradingPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.calculator = mainWindow.getCurrentCalculator();

        this.studentIds = calculator.getCourse()
                .getEnrolledStudents().stream()
                .map(s -> s.getId())
                .collect(Collectors.toList());

        this.assignmentIds = calculator.getAssignments().stream()
                .map(Assignment::getId)
                .collect(Collectors.toList());

        setLayout(new BorderLayout(10, 10));
        initComponents();
    }

    private void initComponents() {
        // Title
        JLabel titleLabel = new JLabel(
                "Course: " + calculator.getCourse().getCode() +
                        " | Semester: " + calculator.getCourse().getTerm().getSeason() +
                        " " + calculator.getCourse().getTerm().getYear(),
                SwingConstants.CENTER
        );
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        // Table of submissions
        tableModel = buildTableModel();
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnSetGradingRule = new JButton("Set Grading Rule");
        JButton btnBeginGrading   = new JButton("Begin Grading");
        buttonPanel.add(btnSetGradingRule);
        buttonPanel.add(btnBeginGrading);
        add(buttonPanel, BorderLayout.SOUTH);

        btnSetGradingRule.addActionListener(e -> {
            mainWindow.setCurrentCalculator(calculator);
            mainWindow.getNavigator().push("setGradingRule");
        });

        btnBeginGrading.addActionListener(e -> {
            updateFinalScores();
            mainWindow.setCurrentCalculator(calculator);
            mainWindow.getNavigator().push("statistics");
        });
    }

    private DefaultTableModel buildTableModel() {
        String[] cols = buildColumnHeaders();
        Object[][] data = buildTableData();
        return new DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
    }

    private String[] buildColumnHeaders() {
        List<String> cols = new ArrayList<>();
        cols.add("Student");
        cols.add("Submitted");
        cols.addAll(assignmentIds);
        cols.add("Final Score");
        return cols.toArray(new String[0]);
    }

    private Object[][] buildTableData() {
        Map<String, Set<String>> submissions = new HashMap<>();
        for (Assignment a : calculator.getAssignments()) {
            for (Submission s : a.getSubmissions()) {
                submissions
                        .computeIfAbsent(s.getStudent().getId(), k -> new HashSet<>())
                        .add(a.getId());
            }
        }
        int rows = studentIds.size();
        int cols = 2 + assignmentIds.size() + 1;
        Object[][] data = new Object[rows][cols];
        for (int i = 0; i < rows; i++) {
            String sid = studentIds.get(i);
            Set<String> done = submissions.getOrDefault(sid, Collections.emptySet());
            data[i][0] = sid;
            data[i][1] = done.size() + "/" + assignmentIds.size();
            for (int j = 0; j < assignmentIds.size(); j++) {
                data[i][2 + j] = done.contains(assignmentIds.get(j)) ? "✓" : "";
            }
            data[i][2 + assignmentIds.size()] = "";
        }
        return data;
    }

    /**
     * After grading rule or weights have changed, recalc and update the final column.
     */
    public void updateFinalScores() {
        Map<String, Double> scores = calculator.calculateAllStudentGrades();
        for (int i = 0; i < studentIds.size(); i++) {
            String sid = studentIds.get(i);
            Double val = scores.getOrDefault(sid, 0.0);
            tableModel.setValueAt(
                    String.format("%.2f", val),
                    i,
                    2 + assignmentIds.size()
            );
        }
    }
}
