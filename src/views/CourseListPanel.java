package views;

import auth.Auth;
import obj.Course;
import obj.Term;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class CourseListPanel extends JPanel {
    private final MainWindow mainWindow;
    private final JPanel semestersPanel;

    public CourseListPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Your Courses");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(new EmptyBorder(20, 20, 20, 0));
        add(title);

        semestersPanel = new JPanel();
        semestersPanel.setLayout(new BoxLayout(semestersPanel, BoxLayout.Y_AXIS));
        semestersPanel.setBackground(Color.WHITE);
        add(semestersPanel);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        refreshCourses();
    }

    private void refreshCourses() {
        semestersPanel.removeAll();

        Auth.UserType userType = mainWindow.getAuth().getUserType();
        List<Course> courses;
        switch (userType) {
            case INSTRUCTOR:
                courses = mainWindow.getAuth().getInstructor().get().getCourses();
                break;
            case STUDENT:
                courses = mainWindow.getAuth().getStudent().get().getEnrolledCourses();
                break;
            case GRADER:
                courses = mainWindow.getAuth().getGrader().get().getCourses();
                break;
            default:
                throw new IllegalStateException("Unexpected user type: " + userType);
        }

        TreeMap<Term, List<Course>> byTerm = new TreeMap<>();
        for (Course c : courses) {
            byTerm.computeIfAbsent(c.getTerm(), t -> new ArrayList<>()).add(c);
        }

        byTerm.forEach((term, list) -> semestersPanel.add(createSemesterPanel(term, list)));

        revalidate();
        repaint();
    }

    private JPanel createSemesterPanel(Term term, List<Course> courses) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        JLabel semesterLabel = new JLabel(term.getName());
        semesterLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        semesterLabel.setBorder(new EmptyBorder(0, 20, 10, 0));

        JPanel grid = new JPanel(new GridLayout(0, 3, 20, 20));
        grid.setBackground(Color.WHITE);
        grid.setBorder(new EmptyBorder(0, 20, 20, 20));

        courses.forEach(c -> grid.add(createCourseCard(c)));
        grid.add(createAddCourseCard());

        panel.add(semesterLabel);
        panel.add(grid);
        return panel;
    }

    private JPanel createAddCourseCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(250, 120));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createDashedBorder(Color.GRAY));

        JLabel plus = new JLabel("+ Add a course");
        plus.setFont(new Font("Arial", Font.BOLD, 14));
        card.add(plus);

        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainWindow.getNavigator().push(EditCoursePanel.getCreateKey(mainWindow));
            }
        });
        return card;
    }

    private JPanel createCourseCard(Course course) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(250, 120));
        card.setBackground(new Color(240, 240, 240));
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JLabel info = new JLabel(
                "<html><b>" + course.getCode() + "</b><br/>" +
                        course.getName() + "</html>");
        info.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel count = new JLabel(
                course.getAssignmentCount() + " assignments",
                SwingConstants.CENTER);
        count.setOpaque(true);
        count.setBackground(new Color(0, 80, 100));
        count.setForeground(Color.WHITE);
        count.setPreferredSize(new Dimension(250, 30));

        card.add(info, BorderLayout.CENTER);
        card.add(count, BorderLayout.SOUTH);

        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                mainWindow.getNavigator().push(CourseViewPanel.getKey(mainWindow, course));
            }
        });
        return card;
    }
}
