package grading;

import java.util.*;
import testclasses.*;

public class TestDataGenerator {
    public static GradeCalculator setupSampleData() {
        testCourse course = new testCourse("CS611-001", "CS611", "Advanced Java", "Spring 2025");

        testAssignment a1 = new testAssignment("a1", course.getCourse_id(), "Homework 1", 100);
        testAssignment a2 = new testAssignment("a2", course.getCourse_id(), "Homework 2", 100);
        testAssignment a3 = new testAssignment("a3", course.getCourse_id(), "Project", 100);

        a1.addSubmission(new testSubmission("a1", "s1", "Code for A1", 90, 0.01));
        a1.addSubmission(new testSubmission("a1", "s2", "Code for A1", 80, 0.02));
        a1.addSubmission(new testSubmission("a1", "s3", "Code for A1", 70, 0.03));

        a2.addSubmission(new testSubmission("a2", "s1", "Code for A2", 85, 0.01));
        a2.addSubmission(new testSubmission("a2", "s2", "Code for A2", 75, 0.02));
        a2.addSubmission(new testSubmission("a2", "s3", "Code for A2", 60, 0.01));

        a3.addSubmission(new testSubmission("a3", "s1", "Project Code", 88, 0.02));
        a3.addSubmission(new testSubmission("a3", "s2", "Project Code", 77, 0.01));
        a3.addSubmission(new testSubmission("a3", "s3", "Project Code", 66, 0.03));

        List<testAssignment> assignments = Arrays.asList(a1, a2, a3);

        GradeCalculator calculator = new GradeCalculator(course, assignments);
        calculator.setAssignmentWeight("a1", 30);
        calculator.setAssignmentWeight("a2", 30);
        calculator.setAssignmentWeight("a3", 40);

        return calculator;
    }
}
