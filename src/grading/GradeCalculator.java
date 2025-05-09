package grading;

import java.io.*;
import java.util.*;

import model.Assignment;
import model.Category;
import model.Course;
import model.Submission;

public class GradeCalculator {
    private Course course;
    private List<Assignment> assignments;
    private Map<String, Double> assignmentWeights;
    private GradingStrategy strategy;

    public GradeCalculator(Course course, List<Assignment> assignments) {
        this.course = course;
        this.assignments = assignments;
        this.assignmentWeights = new HashMap<>();
        this.strategy = new CategoryRatioStrategy(); 
        initializeDefaultWeights();
    }

    private void initializeDefaultWeights() {
        if (assignments == null || assignments.isEmpty()) return;
    
        // clean weight
        assignmentWeights.clear();
    
        for (Assignment a : assignments) {
            Category category = a.getCategory();
    
            // read weight from category, zero by default
            double weight = (category != null) ? category.getWeight() : 0;
    
            assignmentWeights.put(a.getId(), weight);
        }
    
    }

    public void setAssignmentWeight(String assignmentId, double weight) {
        assignmentWeights.put(assignmentId, weight);
    }

    public void setStrategy(GradingStrategy strategy) {
        this.strategy = strategy;
    }

    public double calculateFinalGradeForStudent(String studentId) {
        Map<String, Integer> studentScores = new HashMap<>();
        for (Assignment a : assignments) {
            for (Submission s : a.getSubmissions()) {
                if (s.getStudent().getId().equals(studentId) && s.getGrade().isPresent()) {
                    studentScores.put(a.getId(), s.getGrade().get());
                    break;
                }
            }
        }
        return strategy.calculateFinalGrade(assignments, studentScores, assignmentWeights);
    }
    

    public Map<String, Double> calculateAllStudentGrades() {
        Set<String> studentIds = new HashSet<>();
        for (Assignment a : assignments) {
            for (Submission s : a.getSubmissions()) {
                studentIds.add(s.getStudent().getId());
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

    public Course getCourse() {
        return course;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }
}
