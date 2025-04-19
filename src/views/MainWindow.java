package views;

import javax.swing.JFrame;

public class MainWindow extends JFrame {
  public MainWindow() {
    setTitle("My Application");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(800, 600);
    setLocationRelativeTo(null);
  }
}
