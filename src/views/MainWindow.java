package views;

import auth.Auth;
import grading.GradeCalculator;
import obj.Course;
import obj.Instructor;
import obj.Student;
import store.FileStore;
import store.Store;
import store.StoreExample;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

public class MainWindow extends JFrame {
    private final Store store;
    private final Auth auth;

    // login vs logged-in
    private final CardLayout topLayout = new CardLayout();
    private final JPanel     mainPanel = new JPanel(topLayout);

    private final JButton logoutButton;

    // navigator for all post-login views
    private final Navigator navigator;
    private final JPanel    loggedInPanel;

    // app state
    private List<Course>    courses;
    private Course          currentCourse;
    private GradeCalculator currentCalculator;

    public MainWindow() {
        super("Course Management System");

        // ─── persistence & auth ───────────────────────────────────
        store = new FileStore(System.getProperty("user.dir"), "data.dat");
        auth  = new Auth(store);

        try {
            if (!store.get(Instructor.class, "cpk").isPresent()) {
                StoreExample.populateStore(store);
                store.save();
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // ─── on-close save ────────────────────────────────────────
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                store.save();
                dispose();
                System.exit(0);
            }
        });

        // ─── frame setup ──────────────────────────────────────────
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // logout button (hidden until login)
        logoutButton = new JButton("Logout");
        logoutButton.setVisible(false);
        logoutButton.addActionListener(e -> logout());
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topBar.add(logoutButton);
        add(topBar, BorderLayout.NORTH);

        // mainPanel holds login + loggedIn
        add(mainPanel, BorderLayout.CENTER);
        mainPanel.add(new LoginPanel(this), "login");

        // build the logged-in container
        loggedInPanel = new JPanel(new CardLayout());
        mainPanel.add(loggedInPanel, "loggedIn");

        // ─── navigator setup ─────────────────────────────────────
        navigator = new Navigator(loggedInPanel, (CardLayout) loggedInPanel.getLayout());

        // register every screen by key + Supplier<JPanel>
        navigator.register("courseList",   () -> new CourseListPanel(this));
        //navigator.register("createCourse", () -> new CreateCoursePanel(this));
        // navigator.register("courseView",   () -> new CourseViewPanel(this));
        navigator.register("grading",      () -> new GradingPanel(this));

        navigator.register("assignments", () -> {
            Course course = getCurrentCourse();
            Auth.UserType role = getAuth().getUserType();

            switch (role) {
                case INSTRUCTOR:
                    // full CRUD view
                    return new AssignmentsScreen(this,
                            course,
                            navigator::back);

                case GRADER:
                    // read-only + submissions
                    return new GraderAssignmentsPanel(this,
                            course,
                            navigator::back);

                case STUDENT:
                    // per-student view
                    Student student = getAuth()
                            .getStudent()
                            .orElseThrow(() -> new NoSuchElementException("Student not found"));

                    return new StudentAssignmentsPanel(this,
                            course,
                            student,
                            navigator::back);

                default:
                    throw new IllegalStateException("Unexpected role: " + role);
            }
        });
        navigator.register("roster",       () ->
                new StudentRosterFrame(this,
                        getCurrentCourse(),
                        navigator::back)
        );

        // start at login
        topLayout.show(mainPanel, "login");
        setVisible(true);
    }

    /** Called by LoginPanel when login succeeds. */
    public void onLogin() {
        if (!auth.isLoggedIn()) {
            throw new IllegalStateException("Login failed");
        }

        logoutButton.setVisible(true);

        // load courses & default calc
        courses = store.getAll(Course.class);
        if (!courses.isEmpty()) {
            currentCourse     = courses.get(0);
            currentCalculator = new GradeCalculator(
                    currentCourse,
                    currentCourse.getAssignments()
            );
        }

        topLayout.show(mainPanel, "loggedIn");
        navigator.push("courseList");
    }

    private void logout() {
        auth.logout();
        logoutButton.setVisible(false);
        topLayout.show(mainPanel, "login");
    }

    // ─── accessors ────────────────────────────────────────────────
    public Navigator getNavigator()            { return navigator; }
    public Auth      getAuth()                 { return auth; }
    public List<Course> getCourses()           { return courses; }
    public Course    getCurrentCourse()        { return currentCourse; }
    public void      setCurrentCourse(Course c){ this.currentCourse = c; }
    public GradeCalculator getCurrentCalculator()           { return currentCalculator; }
    public void      setCurrentCalculator(GradeCalculator c){ this.currentCalculator = c; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainWindow::new);
    }

    public Store getStore() {
        return store;
    }
}
