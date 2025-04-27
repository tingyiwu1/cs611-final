package grading;

import java.util.*;
import obj.Assignment;

public interface GradingStrategy {
    double calculateFinalGrade(List<Assignment> assignments, Map<String, Integer> scores, Map<String, Double> weights);
}
