package testclasses;

import java.time.LocalDateTime;
import java.util.*;

public class testAssignment {
    private String assignment_id;
    private String course_id;
    private String name;
    private double points;
    private String category_id;
    private boolean is_published;
    private LocalDateTime deadline;
    private List<testSubmission> submissions = new ArrayList<>();

    public testAssignment(String assignment_id, String course_id, String name, double points) {
        this.assignment_id = assignment_id;
        this.course_id = course_id;
        this.name = name;
        this.points = points;
    }

    public String getAssignment_id() {
        return assignment_id;
    }

    public List<testSubmission> getSubmissions() {
        return submissions;
    }

    public void addSubmission(testSubmission submission) {
        submissions.add(submission);
    }
}
