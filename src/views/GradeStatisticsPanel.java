package views;

import grading.GradeCalculator;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import myUtils.ChartUtils;

public class GradeStatisticsPanel extends JPanel {
    private JLabel minLabel;
    private JLabel maxLabel;
    private JLabel avgLabel;
    private JPanel chartPanel;
    private Map<String, Double> studentGrades = new HashMap<>();
    private GradeCalculator calculator;
    private MainWindow mainWindow;

    public GradeStatisticsPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;

        setLayout(new BorderLayout());

        // 头部信息
        minLabel = new JLabel();
        maxLabel = new JLabel();
        avgLabel = new JLabel();

        JPanel statsPanel = new JPanel(new GridLayout(1, 3));
        statsPanel.add(minLabel);
        statsPanel.add(maxLabel);
        statsPanel.add(avgLabel);

        JButton backButton = new JButton("← Back");
        //backButton.addActionListener(e -> mainWindow.switchPanel("grading"));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(statsPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // 中间柱状图
        
        chartPanel = ChartUtils.createHistogram("Final Grades Distribution", studentGrades);
        
        
        add(chartPanel, BorderLayout.CENTER);
    }

    public void setGradeCalculator(GradeCalculator calculator) {
        this.calculator = calculator;
        updateGradesFromCalculator();
    }

    private void updateGradesFromCalculator() {
        if (calculator == null) return;
        studentGrades = calculator.calculateAllStudentGrades();
    
        double min = studentGrades.values().stream().min(Double::compare).orElse(0.0);
        double max = studentGrades.values().stream().max(Double::compare).orElse(0.0);
        double avg = studentGrades.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    
        minLabel.setText("Min: " + String.format("%.2f", min));
        maxLabel.setText("Max: " + String.format("%.2f", max));
        avgLabel.setText("Average: " + String.format("%.2f", avg));
    
        
        this.remove(chartPanel); //remove old charPanel
        chartPanel = ChartUtils.createHistogram("Final Grades Distribution", studentGrades); 
        this.add(chartPanel, BorderLayout.CENTER); 
        this.revalidate(); 
        this.repaint();    
    }
    
}
