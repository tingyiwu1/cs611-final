package views;

import auth.Auth;
import obj.Course;
import obj.Term;
import views.course.CourseViewPanel;
import views.editcourse.EditCoursePanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class CourseListPanel extends JPanel {
    private static final Dimension CARD_SIZE = new Dimension(200, 150);

    private final MainWindow mainWindow;
    private final JPanel semestersPanel;

    public static String getKey(MainWindow mainWindow) {
        String key = "courseList";
        mainWindow.getNavigator().register(key, () -> new CourseListPanel(mainWindow));
        return key;
    }

    private CourseListPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;

        boolean isInstructor = mainWindow.getAuth().getUserType() == Auth.UserType.INSTRUCTOR;

        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Your Courses");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JButton newCourseButton = new JButton("Create New Course");
        newCourseButton.setFont(newCourseButton.getFont().deriveFont(Font.PLAIN, 16f));
        newCourseButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        newCourseButton.addActionListener(
                e -> mainWindow.getNavigator().push(EditCoursePanel.getCreateKey(mainWindow)));

        newCourseButton.setVisible(isInstructor);
        content.add(newCourseButton);

        semestersPanel = new JPanel();
        semestersPanel.setLayout(new BoxLayout(semestersPanel, BoxLayout.Y_AXIS));

        content.add(semestersPanel);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);
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

        byTerm.descendingMap().forEach((term, list) -> semestersPanel.add(createSemesterPanel(term, list)));

        revalidate();
        repaint();
    }

    private JPanel createSemesterPanel(Term term, List<Course> courses) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel semesterLabel = new JLabel(term.getName());
        semesterLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        semesterLabel.setBorder(new EmptyBorder(0, 20, 10, 0));

        JPanel grid = new JPanel(new GridLayout(0, 3, 20, 20));
        grid.setBorder(new EmptyBorder(0, 20, 20, 20));

        courses.forEach(c -> grid.add(createCourseCard(c)));

        grid.setMaximumSize(grid.getPreferredSize());

        panel.add(semesterLabel);
        panel.add(grid);

        // panel.setBorder(BorderFactory.createCompoundBorder(
        // BorderFactory.createLineBorder(Color.red),
        // panel.getBorder()));

        return panel;
    }

    private JPanel createCourseCard(Course course) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(CARD_SIZE);
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
        count.setPreferredSize(new Dimension(CARD_SIZE.width, 30));

        card.add(info, BorderLayout.CENTER);
        card.add(count, BorderLayout.SOUTH);

        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                mainWindow.getNavigator().push(CourseViewPanel.getKey(mainWindow, course));
            }
        });

        card.setMaximumSize(card.getPreferredSize());

        // card.setBorder(BorderFactory.createCompoundBorder(
        // BorderFactory.createLineBorder(Color.red),
        // card.getBorder()));

        return card;
    }
}
