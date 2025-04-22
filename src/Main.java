import javax.swing.*;
import views.MainWindow;

public class Main {
  public static void main(String[] args) {
<<<<<<< HEAD
    SwingUtilities.invokeLater(() -> new MainWindow());
=======
    SwingUtilities.invokeLater(() -> {
      new MainWindow().setVisible(true);
    });
>>>>>>> main
  }
}
