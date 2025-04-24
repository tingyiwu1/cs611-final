package grading;

import testclasses.testAssignment;
import java.util.*;

public class RatioStrategy implements GradingStrategy {
    @Override
    public double calculateFinalGrade(List<testAssignment> assignments, Map<String, Double> scores, Map<String, Double> weights) {
        double totalWeight = weights.values().stream().mapToDouble(Double::doubleValue).sum();
        if (totalWeight == 0.0) return 0.0;

        double total = 0.0;
        for (testAssignment a : assignments) {
            String id = a.getAssignment_id();
            double score = scores.getOrDefault(id, 0.0);
            double normalizedWeight = weights.getOrDefault(id, 0.0) / totalWeight;
            total += score * normalizedWeight;
        }
        return total;
    }
}
