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
    private List<String> assignmentNames;

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
        
        this.assignmentNames = calculator.getAssignments().stream()
                .map(Assignment::getName)
                .collect(Collectors.toList());

        setLayout(new BorderLayout(10, 10));
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
    
        // ✅ 顶部返回按钮 + 标题横排放置
        JPanel topBar = new JPanel(new BorderLayout());
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> mainWindow.getNavigator().back());
        topBar.add(backButton, BorderLayout.WEST);
    
        JLabel titleLabel = new JLabel(
                "Course: " + calculator.getCourse().getCode() +
                        " | Semester: " + calculator.getCourse().getTerm().getSeason().name().toUpperCase() +
                        " " + calculator.getCourse().getTerm().getYear(),
                SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        topBar.add(titleLabel, BorderLayout.CENTER);
    
        // ✅ 将返回+标题加入到顶部面板中
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(topBar, BorderLayout.NORTH);
    
        // 成绩分布图
        Map<String, Double> scores = calculator.calculateAllStudentGrades();
        JPanel histogramPanel = ChartUtils.createGradeHistogram("Grade Distribution", scores);
        histogramPanel.setPreferredSize(new Dimension(800, 300));
        headerPanel.add(histogramPanel, BorderLayout.CENTER);
    
        // 添加 header 到北侧
        add(headerPanel, BorderLayout.NORTH);
    
        // 表格部分
        tableModel = buildTableModel();
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);
    
        updateFinalScores();
    
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        String studentId = (String) table.getValueAt(row, 0);
                        showStudentScoreDetails(studentId);
                    }
                }
            }
        });
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
    detailDialog.setSize(800, 400);
    detailDialog.setLocationRelativeTo(this);

    JPanel detailPanel = new JPanel(new BorderLayout(10, 10));
    detailPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    List<Assignment> assignments = calculator.getAssignments();
    Map<String, Double> weights = calculator.getAssignmentWeights();

    // Group assignments by category
    Map<String, List<Assignment>> categoryMap = new LinkedHashMap<>();
    for (Assignment a : assignments) {
        String categoryName = a.getCategory().getName();
        categoryMap.computeIfAbsent(categoryName, k -> new ArrayList<>()).add(a);
    }

    // Get category weight and normalize
    Map<String, Double> categoryWeights = new HashMap<>();
    for (String category : categoryMap.keySet()) {
        for (Assignment a : categoryMap.get(category)) {
            double w = weights.getOrDefault(a.getId(), 0.0);
            if (w > 0.0) {
                categoryWeights.put(category, w);
                break;
            }
        }
    }
    double totalCategoryWeight = categoryWeights.values().stream().mapToDouble(Double::doubleValue).sum();
    for (String cat : categoryWeights.keySet()) {
        double raw = categoryWeights.get(cat);
        categoryWeights.put(cat, raw / totalCategoryWeight);
    }

    // Build table rows
    List<Object[]> rows = new ArrayList<>();
    double totalScore = 0.0;

    for (String category : categoryMap.keySet()) {
        List<Assignment> catAssignments = categoryMap.get(category);
        double totalPoints = catAssignments.stream().mapToDouble(Assignment::getPoints).sum();
        double catWeight = categoryWeights.getOrDefault(category, 0.0);

        for (Assignment a : catAssignments) {
            Submission sub = a.getSubmissions().stream()
                    .filter(s -> s.getStudent().getId().equals(studentId))
                    .findFirst().orElse(null);

            int score = (sub != null && sub.getGrade().isPresent()) ? sub.getGrade().get() : 0;
            int full = a.getPoints();
            //double localWeight = full / totalPoints;
            double credit = (score / (double) totalPoints) * catWeight * 100.0;
            String process = String.format("%d/%d * %.5f", score, (int)totalPoints, catWeight);

            rows.add(new Object[]{
                    a.getName(),
                    score,
                    full,
                    String.format("%.0f", totalPoints),
                    String.format("%.9f", catWeight),
                    process,
                    String.format("%.9f", credit)
            });

            totalScore += credit;
        }
    }

    // Add total row
    rows.add(new Object[]{"Total", "", "", "", "", "", String.format("%.9f", totalScore)});

    String[] columns = {"Assignment", "Score", "Total", "Total in Category", "Category Weight", "Computing", "Credit"};
    JTable detailTable = new JTable(rows.toArray(new Object[0][]), columns);
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
        cols.addAll(assignmentNames);
        cols.add("Final Score");
        cols.add("Grade");
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
        int cols = 2 + assignmentIds.size() + 2; // 2 for "Submitted" and "Final Score"
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


    private String toLetterGrade(double score) {
        if (score >= 90) return "A";
        else if (score >= 80) return "B";
        else if (score >= 70) return "C";
        else if (score >= 60) return "D";
        else return "F";
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
            tableModel.setValueAt(
                    toLetterGrade(val),
                    i,
                    2 + assignmentIds.size()+1);
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
