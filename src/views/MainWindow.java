package views;

import obj.Course;
import store.FileStore;
import store.Store;

import javax.swing.*;

import auth.Auth;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.util.ArrayList;
import java.util.List;

public class MainWindow extends JFrame {
    private final Store store;
    private final Auth auth;

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private List<Course> courses;
    private Course currentCourse;

    public MainWindow() {
        store = new FileStore(System.getProperties().getProperty("user.home"), "data.dat");
        auth = new Auth(store);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                store.save();
                MainWindow.this.dispose();
                System.exit(0);
            }
        });

        setTitle("Course Management System");
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
}
