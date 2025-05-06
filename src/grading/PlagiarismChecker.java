package grading;

import obj.Assignment;
import obj.Course;
import obj.Submission;
import strings.StringSimilarity;

import java.util.*;
import java.util.stream.Collectors;

public class PlagiarismChecker {

    /**
     * Compare one submission against *all* other submissions in the same course.
     * @param course the course whose submissions to compare against
     * @param target the submission you want to check
     * @return a Map from each other Submission to its similarity score [0.0–1.0]
     */
    public static Map<Submission, Double> checkAll(Course course, Submission target) {
        Map<Submission, Double> sims = new HashMap<>();
        String tContent = target.getContent();

        for (Assignment a : course.getAssignments()) {
            for (Submission s : a.getSubmissions()) {
                // Skip the target submission itself...
                if (s.getId().equals(target.getId())) continue;
                // and skip any submission made by the same student.
                if (s.getStudent().getId().equals(target.getStudent().getId())) continue;

                double sim = StringSimilarity.similarity(tContent, s.getContent());
                sims.put(s, sim);
            }
        }
        return sims;
    }

    /**
     * Get only those entries whose similarity meets or exceeds `threshold`.
     */
    public static Map<Submission, Double> flagged(
            Course course,
            Submission target,
            double threshold
    ) {
        return checkAll(course, target).entrySet().stream()
                .filter(e -> e.getValue() >= threshold)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    /**
     * Return the top N most similar submissions, sorted descending.
     */
    public static List<Map.Entry<Submission, Double>> topMatches(
            Course course,
            Submission target,
            int topN
    ) {
        return checkAll(course, target).entrySet().stream()
                .sorted(Map.Entry.<Submission,Double>comparingByValue(Comparator.reverseOrder()))
                .limit(topN)
                .collect(Collectors.toList());
    }
}