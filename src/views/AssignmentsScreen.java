package views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import obj.Assignment;

public class AssignmentsScreen extends JPanel {

  private final JPanel assignmentsList;
  private Assignment[] assignments;

  public AssignmentsScreen() {
    setLayout(new BorderLayout());

    JLabel titleLabel = new JLabel("Assignments", JLabel.CENTER);
    add(titleLabel, BorderLayout.PAGE_START);

    assignmentsList = new JPanel();
    assignmentsList.setLayout(new BoxLayout(assignmentsList, BoxLayout.Y_AXIS));

    setSampleData();

    for (Assignment assignment : assignments) {
      JPanel assignmentPanel = new AssignmentRow(assignment);
      assignmentsList.add(assignmentPanel);
      // assignmentsList.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    add(assignmentsList, BorderLayout.CENTER);

    JButton newAssignmentButton = new JButton("Create New Assignment");
    newAssignmentButton.addActionListener(e -> {
      // Handle new assignment action
      System.out.println("Create new assignment button clicked");
    });
    add(newAssignmentButton, BorderLayout.PAGE_END);

    setVisible(true);
  }

  private void setSampleData() {
    assignments = Assignment.getSampleAssignments();
  }

  private class AssignmentRow extends JPanel {
    private final Assignment assignment;

    public AssignmentRow(Assignment assignment) {
      this.assignment = assignment;
      setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
      setSize(200, 50);
      setVisible(true);

      JLabel nameLabel = new JLabel(assignment.name);
      JLabel categoryLabel = new JLabel(assignment.category);
      JLabel pointsLabel = new JLabel(String.valueOf(assignment.points));
      JLabel dueDateLabel = new JLabel(assignment.dueDate.toString());
      JButton editButton = new JButton("Edit");
      editButton.addActionListener(new EditListener());
      JButton submissionsButton = new JButton("Submissions");
      submissionsButton.addActionListener(new SubmissionsListener());

      add(nameLabel);
      add(Box.createRigidArea(new Dimension(5, 0)));
      add(categoryLabel);
      add(Box.createRigidArea(new Dimension(5, 0)));
      add(pointsLabel);
      add(Box.createRigidArea(new Dimension(5, 0)));
      add(dueDateLabel);
      add(Box.createRigidArea(new Dimension(5, 0)));
      add(editButton);
      add(Box.createRigidArea(new Dimension(5, 0)));
      add(submissionsButton);
    }

    private class EditListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.println("Edit assignment: " + assignment.id);
      }
    }

    private class SubmissionsListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.println("View submissions for assignment: " + assignment.id);
      }
    }
  }

}
