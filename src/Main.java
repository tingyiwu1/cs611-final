import javax.swing.*;

import views.MainWindow;

public class Main {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        new MainWindow().setVisible(true);
      }
    });
  }
}