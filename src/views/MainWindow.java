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
    public final Store store;
    public final Auth auth;

    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    private final CardLayout loggedInCardLayout;
    private final JPanel loggedInPanel;

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
        // mainPanel.add(new CreateCoursePanel(this), "createCourse");
        // mainPanel.add(new CourseViewPanel(this), "courseView");

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

    public void setCurrentCourse(Course course) {
    }

    public Course getCurrentCourse() {
        return null;
    }

    public void logout() {
        auth.logout();
        loggedInPanel.removeAll();
        cardLayout.show(mainPanel, "loggedOut");
    }
}
