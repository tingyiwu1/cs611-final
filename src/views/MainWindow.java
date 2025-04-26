package views;

import auth.Auth;
import grading.GradeCalculator;
import java.awt.*;
import java.awt.event.WindowAdapter;
import javax.swing.*;
import obj.Course;
import store.FileStore;
import store.Store;

public class MainWindow extends JFrame {
    public final Store store;
    public final Auth auth;

    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    private final CardLayout loggedInCardLayout;
    private final JPanel loggedInPanel;

    private Course currentCourse;
    private GradeCalculator currentCalculator;


    public MainWindow() {
        store = new FileStore(System.getProperty("user.dir"), "data.dat");
        auth = new Auth(store);

        System.out.println(store);
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

        mainPanel.add(new LoginPanel(this), "loggedOut");

        loggedInCardLayout = new CardLayout();
        loggedInPanel = new JPanel(loggedInCardLayout);

        mainPanel.add(loggedInPanel, "loggedIn");

        cardLayout.show(mainPanel, "loggedOut");
        // loggedInPanel.add(new CreateCoursePanel(this), "createCourse");
        // loggedInPanel.add(new CourseViewPanel(this), "courseView");

        add(mainPanel);
        setVisible(true);
    }

    public void switchPanel(String panelName) {
        loggedInCardLayout.show(loggedInPanel, panelName);
    }

    public void onLogin() {
        if (!auth.isLoggedIn()) {
            throw new IllegalStateException("Not logged in");
        }
        loggedInPanel.removeAll();
        loggedInPanel.add(new CourseListPanel(this), "courseList");
        // loggedInPanel.add(new AssignmentsScreen(), "assignments");

        cardLayout.show(mainPanel, "loggedIn");
        loggedInCardLayout.show(loggedInPanel, "courseList");
    }

    public void openCourse(Course course, GradeCalculator calculator) {
        this.currentCourse = course;
        this.currentCalculator = calculator;
    
        loggedInPanel.add(new CourseViewPanel(this), "courseView");
        switchPanel("courseView");
    }    

    public Course getCurrentCourse() {
        return currentCourse;
    }

    public void logout() {
        auth.logout();
        loggedInPanel.removeAll();
        cardLayout.show(mainPanel, "loggedOut");
    }

    public JPanel getLoggedInPanel() {
        return loggedInPanel;
    }
    

    public void setCurrentCalculator(GradeCalculator calculator) {
        this.currentCalculator = calculator;
    }

    public GradeCalculator getCurrentCalculator() {
        return currentCalculator;
    }
}
