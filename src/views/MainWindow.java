package views;

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
}
