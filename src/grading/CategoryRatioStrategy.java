package grading;

import java.util.*;

import model.Assignment;
import model.Category;

public class CategoryRatioStrategy implements GradingStrategy {
    @Override
    public double calculateFinalGrade(List<Assignment> assignments, Map<String, Integer> scores, Map<String, Double> weights) {
        // Step 1: Group assignments by category
        Map<Category, List<Assignment>> categoryMap = new HashMap<>();
        for (Assignment a : assignments) {
            categoryMap.computeIfAbsent(a.getCategory(), k -> new ArrayList<>()).add(a);
        }

        // Step 2: Get original (non-normalized) category weights
        Map<Category, Double> categoryWeights = new HashMap<>();
        for (Map.Entry<Category, List<Assignment>> entry : categoryMap.entrySet()) {
            Category category = entry.getKey();
            double rawWeight = 0.0;
            for (Assignment a : entry.getValue()) {
                rawWeight = weights.getOrDefault(a.getId(), 0.0);  // assume all assignments in same category share the same weight
                if (rawWeight > 0.0) break;
            }
            categoryWeights.put(category, rawWeight);
        }

        // Step 3: Normalize category weights
        double totalCategoryWeight = categoryWeights.values().stream().mapToDouble(Double::doubleValue).sum();
        if (totalCategoryWeight == 0.0) return 0.0;

        for (Map.Entry<Category, Double> entry : categoryWeights.entrySet()) {
            double normalized = (entry.getValue() / totalCategoryWeight);
            categoryWeights.put(entry.getKey(), normalized);
        }

        // Step 4: Compute final score
        double total = 0.0;
        for (Map.Entry<Category, List<Assignment>> entry : categoryMap.entrySet()) {
            Category category = entry.getKey();
            List<Assignment> catAssignments = entry.getValue();

            double totalPoints = catAssignments.stream()
                    .mapToDouble(Assignment::getPoints)
                    .sum();
            if (totalPoints == 0.0) continue;

            double studentPoints = 0.0;
            for (Assignment a : catAssignments) {
                int grade = scores.getOrDefault(a.getId(), 0);
                studentPoints += grade;
            }

            double ratio = studentPoints / totalPoints;
            double normalizedCategoryWeight = categoryWeights.get(category); // ∈ [0, 1]
            total += ratio * normalizedCategoryWeight;
        }

        return total * 100.0;  // Final score in percentage
    }
}
