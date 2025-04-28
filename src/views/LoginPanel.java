package views;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import obj.User;

public class LoginPanel extends JPanel {
    private MainWindow mainWindow;

    private final JTextField userIdField;
    private final JLabel errorLabel;

    public LoginPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Login", JLabel.CENTER);
        add(titleLabel, BorderLayout.PAGE_START);

        this.userIdField = new JTextField(20);
        userIdField.setMaximumSize(userIdField.getPreferredSize());

        add(createLoginInputPanel());

        this.errorLabel = new JLabel();
        errorLabel.setForeground(java.awt.Color.RED);
        errorLabel.setVisible(false);
        add(errorLabel);

        add(createLoginButton());

    }

    private JPanel createLoginInputPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        panel.add(new JLabel("User ID: "));
        panel.add(userIdField);

        return panel;
    }

    private void displayErrorMessage(String message) {
        if (message == null || message.isEmpty()) {
            errorLabel.setVisible(false);
            return;
        }

        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.revalidate();
        errorLabel.repaint();
    }

    private JButton createLoginButton() {
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userId = userIdField.getText().trim();
                if (userId.isEmpty()) {
                    displayErrorMessage("User ID cannot be empty.");
                    return;
                }

                Optional<User> user = mainWindow.getAuth().login(userId);
                if (user.isPresent()) {
                    displayErrorMessage(null);
                    mainWindow.onLogin();
                } else {
                    displayErrorMessage("User not found. Please try again.");
                }
            }
        });
        return loginButton;
    }
}
