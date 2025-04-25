package views;

import auth.Auth;
import obj.Course;
import obj.User;
import store.FileStore;
import store.Store;
import store.StoreExample;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

public class MainWindow extends JFrame {
    public final Store store;
    public final Auth  auth;

    private final CardLayout cardLayout;
    private final JPanel    mainPanel;

    private final CardLayout loggedInCardLayout;
    private final JPanel     loggedInPanel;

    private List<Course> courses;
    private Course       currentCourse;

    public MainWindow() {
        // 1) Initialize the FileStore & Auth
        store = new FileStore(System.getProperty("user.dir"), "data.dat");
        auth  = new Auth(store);

        // 2) Populate with sample data if it's empty
        try {
            // StoreExample.populateStore will throw if already populated,
            // so guard by checking for one known instructor
            if (store.get(obj.Instructor.class, "cpk").isEmpty()) {
                StoreExample.populateStore(store);
                store.save();
            }
        } catch (ParseException e) {
            e.printStackTrace();
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

        setTitle("Course Management System");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 4) Top‐level cards: loggedOut vs loggedIn
        cardLayout = new CardLayout();
        mainPanel  = new JPanel(cardLayout);

        // 4a) loggedOut = LoginPanel
        mainPanel.add(new LoginPanel(this), "loggedOut");

        // 4b) loggedIn = sub-cards for course screens
        loggedInCardLayout = new CardLayout();
        loggedInPanel      = new JPanel(loggedInCardLayout);
        mainPanel.add(loggedInPanel, "loggedIn");

        add(mainPanel, BorderLayout.CENTER);

        // start on login
        cardLayout.show(mainPanel, "loggedOut");
        setVisible(true);
    }

    /**
     * Called by LoginPanel once login is successful.
     * This is where we load courses from the store and build the post-login UI.
     */
    public void onLogin() {
        if (!auth.isLoggedIn()) {
            throw new IllegalStateException("Login failed");
        }

        // 1) Load *all* courses from the store
        courses = store.getAll(Course.class);

        // default to first one, if any
        if (!courses.isEmpty()) {
            currentCourse = courses.get(0);
        }

        // 2) Build the logged-in panels (fresh each login)
        loggedInPanel.removeAll();
        loggedInPanel.add(new CourseListPanel(this),   "courseList");
        loggedInPanel.add(new CreateCoursePanel(this), "createCourse");
        loggedInPanel.add(new CourseViewPanel(this),   "courseView");

        // 3) Show the logged-in stack
        cardLayout.show(mainPanel, "loggedIn");
        loggedInCardLayout.show(loggedInPanel, "courseList");
    }

    /**
     * Switch among the post-login cards.  E.g. "createCourse", "courseView".
     */
    public void switchPanel(String panelName) {
        loggedInCardLayout.show(loggedInPanel, panelName);
    }

    /**
     * Optional “logout” helper.
     */
    public void logout() {
        auth.logout();
        cardLayout.show(mainPanel, "loggedOut");
    }

    /** Expose to CourseListPanel so it can pull the full course list. */
    public List<Course> getCourses() {
        return courses;
    }

    public void setCurrentCourse(Course c) {
        this.currentCourse = c;
    }

    public Course getCurrentCourse() {
        return currentCourse;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainWindow::new);
    }
}
