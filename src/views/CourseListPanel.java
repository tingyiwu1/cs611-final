package views;

import obj.Course;
import obj.Term;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import auth.Auth;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class CourseListPanel extends JPanel {
    private final MainWindow mainWindow;
    private TreeMap<Term, ArrayList<Course>> coursesByTerm = new TreeMap<>();
    private final JPanel semestersPanel;

    public CourseListPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Your Courses");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(new EmptyBorder(20, 20, 20, 0));
        add(title);

        semestersPanel = new JPanel();
        semestersPanel.setLayout(new BoxLayout(semestersPanel, BoxLayout.Y_AXIS));
        semestersPanel.setBackground(Color.WHITE);

        add(semestersPanel);

        refreshCourses();
    }

    public void refreshCourses() {
        semestersPanel.removeAll();

        Auth.UserType userType = mainWindow.auth.getUserType();

        ArrayList<Course> courses;
        if (userType == Auth.UserType.INSTRUCTOR) {
            courses = mainWindow.auth.getInstructor().get().getCourses();
        } else if (userType == Auth.UserType.STUDENT) {
            courses = mainWindow.auth.getStudent().get().getEnrolledCourses();
        } else if (userType == Auth.UserType.GRADER) {
            courses = mainWindow.auth.getGrader().get().getCourses();
        } else {
            throw new IllegalArgumentException("Unknown user type: " + userType);
        }

        this.coursesByTerm = new TreeMap<>();
        for (Course course : courses) {
            Term term = course.getTerm();
            if (!this.coursesByTerm.containsKey(term)) {
                this.coursesByTerm.put(term, new ArrayList<>());
            }
            this.coursesByTerm.get(term).add(course);
        }

        for (HashMap.Entry<Term, ArrayList<Course>> entry : coursesByTerm.entrySet()) {
            Term term = entry.getKey();
            List<Course> coursesInTerm = entry.getValue();
            semestersPanel.add(createSemesterPanel(term, coursesInTerm));
        }
    }

    private JPanel createSemesterPanel(Term term, List<Course> courses) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        JLabel semesterLabel = new JLabel(term.getName());
        semesterLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        semesterLabel.setBorder(new EmptyBorder(0, 20, 10, 0));

        JPanel gridPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        gridPanel.setBackground(Color.WHITE);
        gridPanel.setBorder(new EmptyBorder(0, 20, 0, 20));

        for (Course course : courses) {
            gridPanel.add(createCourseCard(course));
        }

        gridPanel.add(createAddCourseCard());

        panel.add(semesterLabel);
        panel.add(gridPanel);
        return panel;
    }

    private JPanel createAddCourseCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(250, 120));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createDashedBorder(Color.GRAY));

        JLabel label = new JLabel("+ Add a course");
        label.setFont(new Font("Arial", Font.BOLD, 14));
        card.add(label);

        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mainWindow.switchPanel("createCourse");
            }
        });

        return card;
    }

    private JPanel createCourseCard(Course course) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(250, 120));
        card.setBackground(new Color(240, 240, 240));
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JLabel label = new JLabel("<html><b>" + course.getCode() + "</b><br>" + course.getName() + "</html>");
        label.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel assignmentLabel = new JLabel(course.getAssignmentCount() + " assignments", SwingConstants.CENTER);
        assignmentLabel.setOpaque(true);
        assignmentLabel.setBackground(new Color(0, 80, 100));
        assignmentLabel.setForeground(Color.WHITE);
        assignmentLabel.setPreferredSize(new Dimension(250, 30));

        card.add(label, BorderLayout.CENTER);
        card.add(assignmentLabel, BorderLayout.SOUTH);

        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mainWindow.setCurrentCourse(course); // 设置当前选中课程
                mainWindow.switchPanel("courseView"); // 切换视图
            }
        });

        return card;
    }
}
