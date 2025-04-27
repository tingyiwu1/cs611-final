// AssignmentsScreen.java
package views;

import obj.Assignment;
import obj.Course;

import javax.swing.*;
import java.awt.*;

/**
 * Lists all assignments for a given Course, with Edit and Submissions buttons.
 */
public class AssignmentsScreen extends JPanel {
  private final Course course;
  private final Assignment[] assignments;
  private final JPanel assignmentsList;

  public AssignmentsScreen(Course course) {
    this.course = course;
    // grab all assignments out of the course
    this.assignments = course != null
            ? course.getAssignments().toArray(new Assignment[0])
            : new Assignment[0];

    assignmentsList = new JPanel();
    assignmentsList.setLayout(new BoxLayout(assignmentsList, BoxLayout.Y_AXIS));

    initComponents();
  }

  private void initComponents() {
    setLayout(new BorderLayout(10, 10));

    // Title
    JLabel titleLabel = new JLabel("Assignments for " + course.getCode(), SwingConstants.CENTER);
    titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));
    add(titleLabel, BorderLayout.NORTH);

    // Assignment rows
    for (Assignment a : assignments) {
      AssignmentRow row = new AssignmentRow(a);
      assignmentsList.add(row);
      assignmentsList.add(Box.createRigidArea(new Dimension(0, 5)));
    }
    add(new JScrollPane(assignmentsList), BorderLayout.CENTER);

    // Create button
    JButton newBtn = new JButton("Create New Assignment");
    newBtn.addActionListener(e -> {
      // TODO: open your editor
      System.out.println("Create new assignment clicked");
    });
    JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    south.add(newBtn);
    add(south, BorderLayout.SOUTH);
  }

  private class AssignmentRow extends JPanel {
    AssignmentRow(Assignment a) {
      setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
      setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

      add(new JLabel(a.getName()));
      add(Box.createHorizontalStrut(10));
      add(new JLabel(a.getPoints() + " pts"));
      add(Box.createHorizontalStrut(10));
      add(new JLabel(a.getDueDate().toString()));
      add(Box.createHorizontalGlue());

      JButton edit = new JButton("Edit");
      edit.addActionListener(evt -> {
        // TODO: open editor for 'a'
        System.out.println("Edit " + a.getId());
      });
      add(edit);

      add(Box.createHorizontalStrut(5));
      JButton subs = new JButton("Submissions");
      subs.addActionListener(evt -> openSubmissions(a));
      add(subs);
    }
  }

  private void openSubmissions(Assignment a) {
    SwingUtilities.invokeLater(() -> {
      JFrame frame = new JFrame("Submissions – " + a.getName());
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.getContentPane().add(new SubmissionsScreen(a));
      frame.pack();
      frame.setLocationRelativeTo(this);
      frame.setVisible(true);
    });
  }
}
