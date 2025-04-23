package obj;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

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
            if (assignment.getCategory().getId() == category.getId()) {
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

    public Optional<Employment> getEmployment(String graderId) {
        return store.get(Employment.class, graderId + "-" + getId());
    }

    public Optional<Employment> getEmployment(Grader grader) {
        return getEmployment(grader.getId());
    }

    public Optional<Enrollment> getEnrollment(String studentId) {
        return store.get(Enrollment.class, studentId + "-" + getId());
    }

    public Optional<Enrollment> getEnrollment(Student student) {
        return getEnrollment(student.getId());
    }

    public Category createCategory(String categoryId, String name, int weight) {
        return new Category(store, categoryId, getId(), name, weight);
    }

    public Assignment createAssignment(String assignmentId, String name, String categoryId, int points,
            boolean isPublished, Date dueDate) {
        return new Assignment(store, assignmentId, name, getId(), categoryId, points, isPublished, dueDate);
    }

    public Assignment createAssignment(String assignmentId, String name, Category category, int points,
            boolean isPublished, Date dueDate) {
        return createAssignment(assignmentId, name, category.getId(), points, isPublished, dueDate);
    }

    public Enrollment createEnrollment(String studentId, Enrollment.Status status) {
        return new Enrollment(store, studentId, getId(), status);
    }

    public Enrollment createEnrollment(Student student, Enrollment.Status status) {
        return createEnrollment(student.getId(), status);
    }

    public Enrollment enrollStudent(String studentId) {
        return createEnrollment(studentId, Enrollment.Status.ENROLLED);
    }

    public Enrollment enrollStudent(Student student) {
        return enrollStudent(student.getId());
    }

    public Employment createEmployment(String graderId) {
        return new Employment(store, graderId, getId());
    }

    public Employment createEmployment(Grader grader) {
        return createEmployment(grader.getId());
    }

    @Override
    public String toString() {
        return code + " - " + name;
    }

}
