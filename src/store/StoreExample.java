package store;

import java.text.DateFormat;
import java.text.ParseException;

import obj.Assignment;
import obj.Category;
import obj.Course;
import obj.Enrollment;
import obj.Grader;
import obj.Instructor;
import obj.StoredObjectFactory;
import obj.Student;
import obj.Submission;
import obj.Term;

public class StoreExample {

  public static void populateStore(Store store) throws ParseException {
    System.out.println("Populating store...");

    StoredObjectFactory factory = new StoredObjectFactory(store);

    DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);

    Term spring2025 = factory.createTerm(Term.Season.SPRING, 2025);

    Instructor cpk = factory.createInstructor("cpk", "CPK");
    Grader alice = factory.createGrader("alice", "Alice");
    Grader bob = factory.createGrader("bob", "Bob");
    Student charlie = factory.createStudent("charlie", "Charlie");
    Student dave = factory.createStudent("dave", "Dave");
    Student eve = factory.createStudent("eve", "Eve");

    Course cs611 = cpk.createCourse(
        "cs611-spring2025",
        "CS611",
        "Object Oriented Design and Development",
        spring2025,
        "This is a course description");

    Category cs611Homework = cs611.createCategory("homework", "Homework", 10);
    Category cs611Midterm = cs611.createCategory("midterm", "midterm", 20);

    Assignment cs611Homework1 = cs611Homework.createAssignment("homework1", "Tic-Tac-Toe-I", 0, true,
        dateFormat.parse("01/28/2025"));
    Assignment cs611Homework2 = cs611Homework.createAssignment("homework2", "Game Infrastructure", 100, true,
        dateFormat.parse("02/14/2025"));
    Assignment cs611MidtermExam = cs611Midterm.createAssignment("midterm-written", "Midterm-Written", 70, true,
        dateFormat.parse("03/04/2025"));
    Assignment cs611MidtermPracticum = cs611Midterm.createAssignment("midterm-practicum", "Midterm-Practicum", 50, true,
        dateFormat.parse("03/06/2025"));

    cs611.createEmployment(alice);
    cs611.createEmployment(bob);

    Enrollment charlieCs611 = cs611.enrollStudent(charlie);
    cs611.createEnrollment(dave, Enrollment.Status.DROPPED);
    cs611.enrollStudent(eve);

    Submission charlieTTT = charlieCs611.createSubmission(cs611Homework1, "This is my tic tac toe submission");
    charlieCs611.createSubmission(cs611Homework2, "This is my game infrastructure submission");
    charlieCs611.createSubmission(cs611MidtermExam, "This is my midterm exam submission");
    charlieCs611.createSubmission(cs611MidtermPracticum, "This is my midterm practicum submission");

    charlieTTT.setGrade(80);
    charlieTTT.setSimilarityScore(3);

    Course cs112 = cpk.createCourse(
        "cs112-spring2025",
        "CS112",
        "Introduction to Computer Science II",
        spring2025,
        "This is a course description");

    cs112.createEmployment(alice);
    Enrollment eveCs112 = cs112.enrollStudent(eve);

    Category cs112Homework = cs112.createCategory("homework", "Homework", 10);
    cs112.createCategory("midterm", "midterm", 20);

    Assignment cs112Homework1 = cs112Homework.createAssignment("homework1", "Tic-Tac-Toe-I", 0, true,
        dateFormat.parse("01/28/2025"));
    Assignment cs112Homework2 = cs112Homework.createAssignment("homework2", "Game Infrastructure", 100, true,
        dateFormat.parse("02/14/2025"));

    Submission evePS1 = eveCs112.createSubmission(cs112Homework1, "This is my PS 1 submission part 1");

    eveCs112.createSubmission(cs112Homework2, "This is my PS 1 submission part 2");

    evePS1.setGrade(80);
    evePS1.setSimilarityScore(3);
  }

  public static void main(String[] args) {
    Store store = new FileStore(System.getProperty("user.dir"), "data.dat");
    if (!store.get(Instructor.class, "cpk").isPresent()) {
      try {
        populateStore(store);
      } catch (ParseException e) {
        e.printStackTrace();
      } finally {
        store.save();
      }
    }
    System.out.println(store);
    Instructor cpk = store.get(Instructor.class, "cpk").get();
    System.out.println("Instructor: " + cpk.getName());
    cpk.delete();
    System.out.println(store);
    System.out.println(store.get(Student.class, "charlie"));
  }
}