package views;

import grading.GradeCalculator;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class GradeStatisticsView extends JFrame {
    private JLabel minLabel;
    private JLabel maxLabel;
    private JLabel avgLabel;
    private JPanel chartPanel;
    private Map<String, Double> studentGrades = new HashMap<>();
    private GradeCalculator calculator;

    public GradeStatisticsView() {
        setTitle("Grade Statistics");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        minLabel = new JLabel();
        maxLabel = new JLabel();
        avgLabel = new JLabel();

        JPanel statsPanel = new JPanel(new GridLayout(1, 3));
        statsPanel.add(minLabel);
        statsPanel.add(maxLabel);
        statsPanel.add(avgLabel);
        add(statsPanel, BorderLayout.NORTH);

        chartPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (studentGrades == null || studentGrades.isEmpty()) return;

                int width = getWidth();
                int height = getHeight();
                int barWidth = width / studentGrades.size();

                int i = 0;
                for (Map.Entry<String, Double> entry : studentGrades.entrySet()) {
                    int barHeight = (int) ((entry.getValue() / 100.0) * (height - 50));
                    g.setColor(Color.BLUE);
                    g.fillRect(i * barWidth + 20, height - barHeight - 30, barWidth - 40, barHeight);
                    g.setColor(Color.BLACK);
                    g.drawString(entry.getKey(), i * barWidth + 30, height - 10);
                    i++;
                }
            }
        };

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

        repaint();
    }
}
