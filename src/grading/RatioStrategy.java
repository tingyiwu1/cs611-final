package grading;

import java.util.*;

import model.Assignment;

public class RatioStrategy implements GradingStrategy {
    @Override
    public double calculateFinalGrade(List<Assignment> assignments, Map<String, Integer> scores, Map<String, Double> weights) {
        double totalWeight = weights.values().stream().mapToDouble(Double::doubleValue).sum();
        if (totalWeight == 0.0) return 0.0;

        double total = 0.0;
        for (Assignment a : assignments) {
            String id = a.getId();
            double score = scores.getOrDefault(id, 0);
            double normalizedWeight = weights.getOrDefault(id, 0.0) / totalWeight;
            total += score * normalizedWeight;
        }
        return total;
    }
}
