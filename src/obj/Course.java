package obj;

import java.util.ArrayList;

import store.Store;
import store.StoredObject;

public class Course extends StoredObject {
    private String code;
    private String name;
    private Term term;
    private String description;
    private final ForeignKey<Instructor> instructor = new ForeignKey<>(Instructor.class); // owned by
    private final ForeignSet<Enrollment> enrollments = new ForeignSet<>(Enrollment.class, "course", this); // owned
    private final ForeignSet<Employment> employments = new ForeignSet<>(Employment.class, "course", this); // owned
    private final ForeignSet<Assignment> assignments = new ForeignSet<>(Assignment.class, "course", this); // owned
    private final ForeignSet<Category> categories = new ForeignSet<>(Category.class, "course", this); // owned

    public Course(Store store, String instructorId, String courseId, String code, String name, Term term,
            String description) {
        super(store, courseId);
        this.code = code;
        this.name = name;
        this.term = term;
        this.description = description;
        this.instructor.setId(instructorId);
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
        return instructor.get();
    }

    public ArrayList<Enrollment> getEnrollments() {
        ArrayList<Enrollment> enrollmentsList = new ArrayList<>();
        enrollments.forEach(enrollmentsList::add);
        return enrollmentsList;
    }

    public ArrayList<Student> getEnrolledStudents() {
        ArrayList<Student> students = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getStatus() == Enrollment.Status.ENROLLED) {
                students.add(enrollment.getStudent());
            }
        }
        return students;
    }

    public ArrayList<Grader> getGraders() {
        ArrayList<Grader> graders = new ArrayList<>();
        for (Employment employment : employments) {
            graders.add(employment.getGrader());
        }
        return graders;
    }

    public ArrayList<Assignment> getAssignments() {
        ArrayList<Assignment> assignmentsList = new ArrayList<>();
        assignments.forEach(assignmentsList::add);
        return assignmentsList;
    }

    public ArrayList<Assignment> getAssignmentsInCategory(Category category) {
        ArrayList<Assignment> assignmentsInCategory = new ArrayList<>();
        for (Assignment assignment : assignments) {
            if (assignment.getCategory() == category) {
                assignmentsInCategory.add(assignment);
            }
        }
        return assignmentsInCategory;
    }

    public int getAssignmentCount() {
        return assignments.count();
    }

    public ArrayList<Category> getCategories() {
        ArrayList<Category> categoriesList = new ArrayList<>();
        categories.forEach(categoriesList::add);
        return categoriesList;
    }

    @Override
    public String toString() {
        return code + " - " + name;
    }

}
