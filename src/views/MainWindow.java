package views;

import auth.Auth;
import obj.Instructor;
import store.FileStore;
import store.Store;
import store.StoreExample;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;

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

        topLayout.show(mainPanel, "loggedIn");
        navigator.push(CourseListPanel.getKey(this));
    }

    private void logout() {
        auth.logout();
        logoutButton.setVisible(false);
        topLayout.show(mainPanel, "login");
    }

    // ─── accessors ────────────────────────────────────────────────
    public Navigator getNavigator()            { return navigator; }
    public Auth      getAuth()                 { return auth; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainWindow::new);
    }

    public Store getStore() {
        return store;
    }
}
