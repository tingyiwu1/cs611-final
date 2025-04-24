package grading;

import testclasses.testAssignment;
import java.util.*;

public interface GradingStrategy {
    double calculateFinalGrade(List<testAssignment> assignments, Map<String, Double> scores, Map<String, Double> weights);
}
