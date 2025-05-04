package views;

import grading.GradeCalculator;
import obj.Assignment;
import obj.Course;
import obj.Submission;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import myUtils.ChartUtils;
import obj.Assignment;
import obj.Student;
import obj.Submission;

/**
 * Panel for instructors to view and grade student submissions.
 */
public class GradingPanel extends JPanel {
    private final MainWindow mainWindow;
    private final Course course;
    private final GradeCalculator calculator;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<String> studentIds;
    private List<String> assignmentIds;

    public static String getKey(MainWindow mainWindow, Course course) {
        String key = "grading:" + course.getId();
        mainWindow.getNavigator().register(key, () -> new GradingPanel(mainWindow, course));
        return key;
    }

    private GradingPanel(MainWindow mainWindow, Course course) {
        this.mainWindow = mainWindow;
        this.course = course;
        this.calculator = new GradeCalculator(course, course.getAssignments());

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
                SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

    // 计算当前学生分数
    Map<String, Double> scores = calculator.calculateAllStudentGrades();

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnSetGradingRule = new JButton("Set Grading Rule");
        JButton btnBeginGrading = new JButton("Begin Grading");
        buttonPanel.add(btnSetGradingRule);
        buttonPanel.add(btnBeginGrading);
        add(buttonPanel, BorderLayout.SOUTH);

        // btnSetGradingRule.addActionListener(e -> {
        //     mainWindow.setCurrentCalculator(calculator);
        //     mainWindow.getNavigator().push("setGradingRule");
        // });

        // btnBeginGrading.addActionListener(e -> {
        //     updateFinalScores();
        //     mainWindow.setCurrentCalculator(calculator);
        //     mainWindow.getNavigator().push("statistics");
        // });
    }
    private void showStudentScoreDetails(String studentId) {
        Student student = calculator.getCourse().getEnrolledStudents().stream()
                .filter(s -> s.getId().equals(studentId))
                .findFirst()
                .orElse(null);

        if (student == null) {
            JOptionPane.showMessageDialog(this, "Student not found.");
            return;
        }

        JDialog detailDialog = new JDialog(mainWindow, "Score Details for " + studentId, true);
        detailDialog.setSize(500, 300);
        detailDialog.setLocationRelativeTo(this);

        JPanel detailPanel = new JPanel(new BorderLayout(10, 10));
        detailPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 创建详细得分表格
        List<Assignment> assignments = calculator.getAssignments();
        String[] columns = {"Assignment Name", "Score", "Weight", "Normalized Weight", "Credit"};
        Object[][] data = new Object[assignments.size() + 1][5]; // 多一行用于总成绩
        
        double totalWeight = assignments.stream()
                .mapToDouble(a -> calculator.getAssignmentWeights().getOrDefault(a.getId(), 0.0))
                .sum();
        
        double totalCredit = 0.0;
        
        for (int i = 0; i < assignments.size(); i++) {
            Assignment a = assignments.get(i);
            Submission sub = a.getSubmissions().stream()
                    .filter(s -> s.getStudent().getId().equals(studentId))
                    .findFirst()
                    .orElse(null);
        
            String scoreText;
            double credit = 0.0;
        
            double weight = calculator.getAssignmentWeights().getOrDefault(a.getId(), 0.0);
            double normalizedWeight = (totalWeight == 0) ? 0 : (weight / totalWeight) * 100.0;
        
            if (sub != null && sub.getGrade().isPresent()) {
                int grade = sub.getGrade().get();
                scoreText = String.format("%.2f / %.2f", (double) grade, (double) a.getPoints());
                credit = grade * (normalizedWeight / 100.0);
            } else {
                scoreText = "0 / " + a.getPoints();
            }
        
            totalCredit += credit;
        
            data[i][0] = a.getName();
            data[i][1] = scoreText;
            data[i][2] = String.format("%.2f%%", weight);
            data[i][3] = String.format("%.2f", normalizedWeight);
            data[i][4] = String.format("%.2f", credit);
        }
        
        // ✅ 添加最后一行显示总成绩
        data[assignments.size()][0] = "Total Score";
        data[assignments.size()][1] = "";
        data[assignments.size()][2] = "";
        data[assignments.size()][3] = "";
        data[assignments.size()][4] = String.format("%.2f", totalCredit);
        

        JTable detailTable = new JTable(data, columns);
        detailTable.setEnabled(false);
        detailPanel.add(new JScrollPane(detailTable), BorderLayout.CENTER);

        detailDialog.setContentPane(detailPanel);
        detailDialog.setVisible(true);
    }


    private DefaultTableModel buildTableModel() {
        String[] cols = buildColumnHeaders();
        Object[][] data = buildTableData();
        return new DefaultTableModel(data, cols) {
            @Override
            public boolean isCellEditable(int row, int col) {
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
     * After grading rule or weights have changed, recalc and update the final
     * column.
     */
    public void updateFinalScores() {
        Map<String, Double> scores = calculator.calculateAllStudentGrades();
        for (int i = 0; i < studentIds.size(); i++) {
            String sid = studentIds.get(i);
            Double val = scores.getOrDefault(sid, 0.0);
            tableModel.setValueAt(
                    String.format("%.2f", val),
                    i,
                    2 + assignmentIds.size());
        }
    
        // update histogram
        JPanel histogramPanel = ChartUtils.createGradeHistogram("Grade Distribution", scores);
        histogramPanel.setPreferredSize(new Dimension(800, 300));
    
        // get top panel
        JPanel topPanel = (JPanel) getComponent(0);
        topPanel.remove(1); // remove old histogram
        topPanel.add(histogramPanel, BorderLayout.CENTER);
        topPanel.revalidate();
        topPanel.repaint();
    }
    
}
