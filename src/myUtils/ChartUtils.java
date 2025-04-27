package myUtils;

import java.awt.*;
import java.util.Map;
import javax.swing.*;

public class ChartUtils {

    /**
     * Create a histogram chart panel based on student grades.
     *
     * @param title the chart title
     * @param studentGrades map from student ID to final grade
     * @return a JPanel containing the histogram
     */
    public static JPanel createGradeHistogram(String title, Map<String, Double> studentGrades) {
        return new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (studentGrades == null || studentGrades.isEmpty()) return;

                // Step 0: Draw title
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 20));
                FontMetrics fmTitle = g.getFontMetrics();
                int titleWidth = fmTitle.stringWidth(title);
                g.drawString(title, (getWidth() - titleWidth) / 2, 30);

                // Step 1: Prepare histogram bins
                int[] bins = new int[10]; // 10 bins for [0-10), [10-20), ..., [90-100]

                for (double grade : studentGrades.values()) {
                    int index = Math.min((int) (grade / 10), 9);
                    bins[index]++;
                }

                // Step 2: Draw histogram
                int width = getWidth();
                int height = getHeight();
                int barWidth = width / bins.length;

                int maxCount = 0;
                for (int count : bins) {
                    maxCount = Math.max(maxCount, count);
                }
                if (maxCount == 0) maxCount = 1;

                g.setFont(new Font("Arial", Font.PLAIN, 12));

                for (int i = 0; i < bins.length; i++) {
                    int barHeight = (int) ((bins[i] * 1.0 / maxCount) * (height - 100)); // leave space for title and labels
                    g.setColor(Color.BLUE);
                    g.fillRect(i * barWidth + 10, height - barHeight - 30, barWidth - 20, barHeight);

                    // draw bin label
                    g.setColor(Color.BLACK);
                    String label = (i * 10) + "-" + ((i + 1) * 10);
                    FontMetrics fm = g.getFontMetrics();
                    int labelWidth = fm.stringWidth(label);
                    g.drawString(label, i * barWidth + (barWidth - labelWidth) / 2, height - 10);

                    // draw count on top of bar
                    String countStr = String.valueOf(bins[i]);
                    int countWidth = fm.stringWidth(countStr);
                    g.drawString(countStr, i * barWidth + (barWidth - countWidth) / 2, height - barHeight - 35);
                }
            }
        };
    }
}
