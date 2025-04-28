//// GraderAssignmentsPanel.java
//package views;
//
//import obj.Assignment;
//import obj.Course;
//
//import javax.swing.*;
//import java.awt.*;
//
///**
// * A read‐only assignments list for graders: you can only view submissions.
// */
//public class GraderAssignmentsPanel extends JPanel {
//    private final Course course;
//
//    public GraderAssignmentsPanel(Course course) {
//        this.course = course;
//        initComponents();
//    }
//
//    private void initComponents() {
//        setLayout(new BorderLayout(10,10));
//
//        // Title
//        JLabel title = new JLabel("Assignments for " + course.getCode(), SwingConstants.CENTER);
//        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
//        add(title, BorderLayout.NORTH);
//
//        // List of assignments
//        JPanel list = new JPanel();
//        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
//        for (Assignment a : course.getAssignments()) {
//            JPanel row = new JPanel(new BorderLayout(5,5));
//            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
//
//            // Info: name + points
//            JLabel info = new JLabel(a.getName() + " — " + a.getPoints() + " pts");
//            row.add(info, BorderLayout.WEST);
//
//            // Only Submissions button
//            JButton subs = new JButton("Submissions");
//            subs.addActionListener(e -> openSubmissions(a));
//            row.add(subs, BorderLayout.EAST);
//
//            list.add(row);
//            list.add(Box.createVerticalStrut(5));
//        }
//        add(new JScrollPane(list), BorderLayout.CENTER);
//    }
//
//    private void openSubmissions(Assignment a) {
//        SwingUtilities.invokeLater(() -> {
//            JFrame frame = new JFrame("Submissions – " + a.getName());
//            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//            frame.getContentPane().add(new SubmissionsScreen(a));
//            frame.pack();
//            frame.setLocationRelativeTo(this);
//            frame.setVisible(true);
//        });
//    }
//}
