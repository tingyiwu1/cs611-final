package grading;

import java.io.*;
import java.util.*;

import obj.Assignment;
import obj.Course;
import testclasses.*;

public class GradeCalculator {
    private testCourse course;
    private List<testAssignment> assignments;
    private Map<String, Double> assignmentWeights;
    private GradingStrategy strategy;

    public GradeCalculator(testCourse course, List<testAssignment> assignments) {
        this.course = course;
        this.assignments = assignments;
        this.assignmentWeights = new HashMap<>();
        this.strategy = new RatioStrategy(); // 默认策略
    }

    public void setAssignmentWeight(String assignmentId, double weight) {
        assignmentWeights.put(assignmentId, weight);
    }

    public void setStrategy(GradingStrategy strategy) {
        this.strategy = strategy;
    }

    public double calculateFinalGradeForStudent(String studentId) {
        Map<String, Double> studentScores = new HashMap<>();
        for (testAssignment a : assignments) {
            for (testSubmission s : a.getSubmissions()) {
                if (s.getStudent_id().equals(studentId)) {
                    studentScores.put(a.getAssignment_id(), s.getGrade());
                    break;
                }
            }
        }
        return strategy.calculateFinalGrade(assignments, studentScores, assignmentWeights);
    }

    public Map<String, Double> calculateAllStudentGrades() {
        Set<String> studentIds = new HashSet<>();
        for (testAssignment a : assignments) {
            for (testSubmission s : a.getSubmissions()) {
                studentIds.add(s.getStudent_id());
            }
        }

        Map<String, Double> result = new HashMap<>();
        for (String sid : studentIds) {
            result.put(sid, calculateFinalGradeForStudent(sid));
        }
        return result;
    }
    
    public Map<String, Double> getAssignmentWeights() {
        return assignmentWeights;
    }
    
    public void exportGradesToCSV(String filename) throws IOException {
        Map<String, Double> grades = calculateAllStudentGrades();
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Student ID,Final Grade");
            for (Map.Entry<String, Double> entry : grades.entrySet()) {
                writer.printf("%s,%.2f%n", entry.getKey(), entry.getValue());
            }
        }
    }

    public testCourse getCourse() {
    return course;
    }

    public List<testAssignment> getAssignments() {
        return assignments;
    }
}
