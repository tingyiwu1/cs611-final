package views;

<<<<<<< HEAD
import obj.Course;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainWindow extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    private List<Course> courses;
    private Course currentCourse;

    public MainWindow() {
        setTitle("Course Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        initCourses();  // 初始化课程列表

        mainPanel.add(new CourseListPanel(this, courses), "courseList");
        mainPanel.add(new CreateCoursePanel(this), "createCourse");
        mainPanel.add(new CourseViewPanel(this), "courseView");

        add(mainPanel);

        setVisible(true);
        switchPanel("courseList");
    }

    private void initCourses() {
        courses = new ArrayList<>();
        courses.add(new Course("CS 581", "Computational Fabrication", "Spring 2025", 15));
        courses.add(new Course("CS 611", "Object Oriented Design and Development", "Spring 2025", 7));
        courses.add(new Course("MA230", "Honors-Level Vector Calculus", "Spring 2025", 12));
        courses.add(new Course("CAS CS 552", "Operating Systems", "Fall 2024", 4));
        courses.add(new Course("CS 582", "Geometry Processing", "Fall 2024", 21));
        courses.add(new Course("CS480/680", "Introduction to Computer Graphics", "Fall 2024", 13));
    }

    public void switchPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }

    public void setCurrentCourse(Course course) {
        this.currentCourse = course;
    }

    public Course getCurrentCourse() {
        return currentCourse;
    }
=======
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainWindow extends JFrame {
  public MainWindow() {
    setTitle("My Application");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(800, 600);
    setLocationRelativeTo(null);

    JPanel screen = new AssignmentsScreen();
    add(screen);
    setVisible(true);
  }
>>>>>>> main
}
