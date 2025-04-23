package obj;

import java.util.ArrayList;
import java.util.HashMap;

import store.Store;
import store.StoredObject;

public class Course extends StoredObject {
    private final String courseId;
    private String code;
    private String name;
    private Term term;
    private String description;
    private Instructor instructor; // owned by
    private HashMap<String, Enrollment> enrollments; // deletes
    private HashMap<String, Grader> graders;
    private HashMap<String, Assignment> assignments; // deletes

    public Course(Store store, String courseId, String code, String name, Term term, String description) {
        super(store);
        this.courseId = courseId;
        this.code = code;
        this.name = name;
        this.term = term;
        this.description = description;
        this.enrollments = new HashMap<>();
        this.graders = new HashMap<>();
        this.assignments = new HashMap<>();
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Term getTerm() {
        return term;
    }

    public String getDescription() {
        return description;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public ArrayList<Enrollment> getEnrollments() {
        return new ArrayList<>(enrollments.values());
    }

    public ArrayList<Student> getEnrolledStudents() {
        ArrayList<Student> students = new ArrayList<>();
        for (Enrollment enrollment : enrollments.values()) {
            if (enrollment.status == Enrollment.Status.ENROLLED) {
                students.add(enrollment.student);
            }
        }
        return students;
    }

    public ArrayList<Grader> getGraders() {
        return new ArrayList<>(graders.values());
    }

    public ArrayList<Assignment> getAssignments() {
        return new ArrayList<>(assignments.values());
    }

    public ArrayList<Assignment> getAssignmentsInCategory(Category category) {
        ArrayList<Assignment> assignmentsInCategory = new ArrayList<>();
        for (Assignment assignment : assignments.values()) {
            if (assignment.getCategory() == category) {
                assignmentsInCategory.add(assignment);
            }
        }
        return assignmentsInCategory;
    }

    public int getAssignmentCount() {
        return assignments.size();
    }

    @Override
    public String toString() {
        return code + " - " + name;
    }

    @Override
    public String getId() {
        return courseId;
    }
}
