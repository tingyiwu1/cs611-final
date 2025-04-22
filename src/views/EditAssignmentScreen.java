package views;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import obj.Assignment;

public class EditAssignmentScreen extends JPanel {
  private final Assignment assignment;

  public EditAssignmentScreen(Assignment assignment) {
    this.assignment = assignment;

    setLayout(new BorderLayout());

    JLabel titleLabel = new JLabel("Edit Assignment", JLabel.CENTER);
    add(titleLabel, BorderLayout.PAGE_START);
  }
}
