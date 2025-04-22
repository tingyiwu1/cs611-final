import javax.swing.*;

import views.MainWindow;

public class Main {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      new MainWindow().setVisible(true);
    });
  }
}