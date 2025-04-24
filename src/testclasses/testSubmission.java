package testclasses;

public class testSubmission {
    private String assignment_id;
    private String student_id;
    private String assignment_data;
    private double grade;
    private double similarity_score;

    public testSubmission(String assignment_id, String student_id, String assignment_data, double grade, double similarity_score) {
        this.assignment_id = assignment_id;
        this.student_id = student_id;
        this.assignment_data = assignment_data;
        this.grade = grade;
        this.similarity_score = similarity_score;
    }

    public String getAssignment_id() {
        return assignment_id;
    }

    public String getStudent_id() {
        return student_id;
    }

    public double getGrade() {
        return grade;
    }
}
