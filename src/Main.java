import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.*;

import grading.GradeCalculator;
import grading.TestDataGenerator;
import views.CourseOverviewView;
import views.MainWindow;


public class Main {
  public static void main(String[] args) {
    //SwingUtilities.invokeLater(() -> new MainWindow());
      SwingUtilities.invokeLater(() -> {
        GradeCalculator calculator = TestDataGenerator.setupSampleData();

            // 获取课程名
          String courseName = calculator.getCourse().getCourse_number();

          // 获取学生列表（从所有 submission 中提取）
          Set<String> studentIds = calculator.getAssignments().stream()
              .flatMap(a -> a.getSubmissions().stream())
              .map(s -> s.getStudent_id())
              .collect(Collectors.toSet());

          CourseOverviewView view = new CourseOverviewView(
              calculator,
              courseName,
              new ArrayList<>(studentIds)
          );

          view.setVisible(true);
      });
  }
}
