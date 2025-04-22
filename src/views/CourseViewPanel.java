package views;

import javax.swing.*;
import obj.Course;
import java.awt.*;

public class CourseViewPanel extends JPanel {
    private MainWindow mainWindow;
    private JLabel titleLabel;
    private JTextArea infoArea;

    public CourseViewPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setLayout(new BorderLayout());
        titleLabel = new JLabel("", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        infoArea = new JTextArea();
        infoArea.setFont(new Font("Arial", Font.PLAIN, 18));
        infoArea.setEditable(false);
        infoArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton back = new JButton("Back");
        back.addActionListener(e -> mainWindow.switchPanel("courseList"));

        add(titleLabel, BorderLayout.NORTH);
        add(infoArea, BorderLayout.CENTER);
        add(back, BorderLayout.SOUTH);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            updateCourseDetails();
        }
        super.setVisible(visible);
    }

    private void updateCourseDetails() {
        Course course = mainWindow.getCurrentCourse();
        if (course != null) {
            titleLabel.setText(course.getCode());
            infoArea.setText(
                "Course Name: " + course.getName() +
                "\nSemester: " + course.getSemester() +
                "\nAssignments: " + course.getAssignmentCount()
            );
        }
    }
}
