//package views;
//
//import javax.swing.*;
//import java.awt.*;
//
//public class CreateCoursePanel extends JPanel {
//    public CreateCoursePanel(MainWindow mainWindow) {
//        setLayout(new GridLayout(4, 2, 10, 10));
//
//        JTextField nameField = new JTextField();
//        JTextField numField = new JTextField();
//        JTextField semesterField = new JTextField();
//
//        add(new JLabel("Course Name:"));
//        add(nameField);
//        add(new JLabel("Course Number:"));
//        add(numField);
//        add(new JLabel("Semester:"));
//        add(semesterField);
//
//        JButton save = new JButton("Save");
//        save.addActionListener(e -> JOptionPane.showMessageDialog(this, "Course saved!"));
//
//        JButton back = new JButton("Back");
//        back.addActionListener(e -> mainWindow.switchPanel("courseList"));
//
//        add(save);
//        add(back);
//    }
//}
