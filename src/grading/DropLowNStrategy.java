package grading;

import java.util.*;
import java.util.stream.Collectors;

import model.Assignment;

public class DropLowNStrategy implements GradingStrategy {
    private int n;

    public DropLowNStrategy(int n) {
        this.n = n;
    }

    @Override
    public double calculateFinalGrade(List<Assignment> assignments, Map<String, Integer> scores, Map<String, Double> weights) {
        List<Map.Entry<String, Integer>> sorted = scores.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .skip(n)
                .collect(Collectors.toList());

        double totalWeight = sorted.stream()
                .mapToDouble(e -> weights.getOrDefault(e.getKey(), 0.0))
                .sum();
        if (totalWeight == 0.0) return 0.0;

        double finalGrade = 0.0;
        for (Map.Entry<String, Integer> entry : sorted) {
            String aid = entry.getKey();
            double score = entry.getValue();
            double normalizedWeight = weights.getOrDefault(aid, 0.0) / totalWeight;
            finalGrade += score * normalizedWeight;
        }

        return finalGrade;
    }
}
