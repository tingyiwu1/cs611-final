package views;

import auth.Auth;
import grading.GradeCalculator;
import obj.Course;
import obj.Instructor;
import store.FileStore;
import store.Store;
import store.StoreExample;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.util.List;

public class MainWindow extends JFrame {
    public final Store store;
    public final Auth auth;

    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    private final CardLayout loggedInCardLayout;
    private final JPanel loggedInPanel;

    private final JButton logoutButton;

    private List<Course> courses;
    private Course currentCourse;
    private GradeCalculator currentCalculator;

    public MainWindow() {
        // 1) Initialize persistence and auth
        store = new FileStore(System.getProperty("user.dir"), "data.dat");
        auth = new Auth(store);

        // 2) Populate sample data if needed
        try {
            if (!store.get(Instructor.class, "cpk").isPresent()) {
                StoreExample.populateStore(store);
                store.save();
            }
        } catch (ParseException e) {
            throw new RuntimeException("Failed to populate sample data", e);
        }

        // 3) Save on close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                store.save();
                dispose();
                System.exit(0);
            }
        });

        // 4) Frame settings
        setTitle("Course Management System");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 5) Logout button at top, hidden until after login
        logoutButton = new JButton("Logout");
        logoutButton.setVisible(false);
        logoutButton.addActionListener(e -> logout());
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topBar.add(logoutButton);
        add(topBar, BorderLayout.NORTH);

        // 6) Top‐level card layout: loggedOut vs loggedIn
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 6a) loggedOut = login screen
        mainPanel.add(new LoginPanel(this), "loggedOut");

        // 6b) loggedIn = a second card layout for post-login views
        loggedInCardLayout = new CardLayout();
        loggedInPanel = new JPanel(loggedInCardLayout);
        mainPanel.add(loggedInPanel, "loggedIn");

        add(mainPanel, BorderLayout.CENTER);

        // show login first
        cardLayout.show(mainPanel, "loggedOut");
        setVisible(true);
    }

    /**
     * Called by LoginPanel when login succeeds.
     */
    public void onLogin() {
        if (!auth.isLoggedIn()) {
            throw new IllegalStateException("Login failed");
        }

        // show logout now that we're in
        logoutButton.setVisible(true);

        // load all courses
        courses = store.getAll(Course.class);
        if (!courses.isEmpty()) {
            currentCourse = courses.get(0);
            currentCalculator = new GradeCalculator(
                    currentCourse,
                    currentCourse.getAssignments());
        }

        // rebuild the logged-in UI
        loggedInPanel.removeAll();
        loggedInPanel.add(new CourseListPanel(this), "courseList");
        loggedInPanel.add(new CreateCoursePanel(this), "createCourse");
        loggedInPanel.add(new CourseViewPanel(this), "courseView");

        // switch to loggedIn → courseList
        cardLayout.show(mainPanel, "loggedIn");
        loggedInCardLayout.show(loggedInPanel, "courseList");
    }

    public void openCourse(Course course, GradeCalculator calculator) {
        this.currentCourse = course;
        this.currentCalculator = calculator;

        // ensure courseView is updated and shown
        loggedInPanel.add(new CourseViewPanel(this), "courseView");
        switchPanel("courseView");
    }

    public void openCourseGrading(Course course) {
        this.currentCourse = course;
        this.currentCalculator = new GradeCalculator(
                currentCourse,
                currentCourse.getAssignments());

        loggedInPanel.add(new GradingPanel(this), "grading");
        switchPanel("grading");
    }

    /**
     * Switch among post-login cards: "courseList", "createCourse", "courseView".
     */
    public void switchPanel(String panelName) {
        loggedInCardLayout.show(loggedInPanel, panelName);
    }

    /**
     * Log out and return to login screen.
     */
    public void logout() {
        auth.logout();
        logoutButton.setVisible(false);
        loggedInPanel.removeAll();
        cardLayout.show(mainPanel, "loggedOut");
    }

    // Expose state to child panels:
    public List<Course> getCourses() {
        return courses;
    }

    public Course getCurrentCourse() {
        return currentCourse;
    }

    public void setCurrentCourse(Course c) {
        this.currentCourse = c;
    }

    public GradeCalculator getCurrentCalculator() {
        return currentCalculator;
    }

    public void setCurrentCalculator(GradeCalculator calc) {
        this.currentCalculator = calc;
    }

    public JPanel getLoggedInPanel() {
        return loggedInPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainWindow::new);
    }
}
