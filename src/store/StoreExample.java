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

    Course cs611 = factory.createCourse(
        cpk.getId(),
        "cs611-spring2025",
        "CS611",
        "Object Oriented Design and Development",
        spring2025,
        "This is a course description");

    Category cs611Homework = factory.createCategory("homework", cs611.getId(), "Homework", 10);
    Category cs611Midterm = factory.createCategory("midterm", cs611.getId(), "midterm", 20);

    Assignment cs611Homework1 = factory.createAssignment(
        "homework1",
        "Tic-Tac-Toe-I",
        cs611.getId(),
        cs611Homework.getId(),
        0,
        true,
        dateFormat.parse("01/28/2025"));
    Assignment cs611Homework2 = factory.createAssignment(
        "homework2",
        "Game Infrastructure",
        cs611.getId(),
        cs611Homework.getId(),
        100,
        true,
        dateFormat.parse("02/14/2025"));
    Assignment cs611MidtermExam = factory.createAssignment(
        "midterm-written",
        "Midterm-Written",
        cs611.getId(),
        cs611Midterm.getId(),
        70,
        true,
        dateFormat.parse("03/04/2025"));
    Assignment cs611MidtermPracticum = factory.createAssignment(
        "midterm-practicum",
        "Midterm-Practicum",
        cs611.getId(),
        cs611Midterm.getId(),
        50,
        true,
        dateFormat.parse("03/06/2025"));

    factory.createEmployment(alice.getId(), cs611.getId());
    factory.createEmployment(bob.getId(), cs611.getId());
    factory.createEnrollment(charlie.getId(), cs611.getId(), Enrollment.Status.ENROLLED);
    factory.createEnrollment(dave.getId(), cs611.getId(), Enrollment.Status.DROPPED);
    factory.createEnrollment(eve.getId(), cs611.getId(), Enrollment.Status.WAITLISTED);

    Submission charlieTTT = factory.createSubmission(cs611Homework1.getId(), charlie.getId(),
        "This is my tic tac toe submission");
    factory.createSubmission(cs611Homework2.getId(), charlie.getId(), "This is my game infrastructure submission");
    factory.createSubmission(cs611MidtermExam.getId(), charlie.getId(), "This is my midterm exam submission");
    factory.createSubmission(cs611MidtermPracticum.getId(), charlie.getId(), "This is my midterm practicum submission");

    charlieTTT.setGrade(80);
    charlieTTT.setSimilarityScore(3);

    Course cs112 = factory.createCourse(
        cpk.getId(),
        "cs112-spring2025",
        "CS112",
        "Introduction to Computer Science II",
        spring2025,
        "This is a course description");

    factory.createEmployment(alice.getId(), cs112.getId());
    factory.createEnrollment(eve.getId(), cs112.getId(), Enrollment.Status.ENROLLED);

    Category cs112Homework = factory.createCategory("homework", cs112.getId(), "Homework", 10);
    Category cs112Midterm = factory.createCategory("midterm", cs112.getId(), "midterm", 20);

    Assignment cs112Homework1 = factory.createAssignment(
        "ps1p1",
        "PS 1: Part I",
        cs112.getId(),
        cs112Homework.getId(),
        100,
        true,
        dateFormat.parse("01/28/2025"));
    Assignment cs112Homework2 = factory.createAssignment(
        "ps1p2",
        "PS 1: Part II",
        cs112.getId(),
        cs112Homework.getId(),
        100,
        true,
        dateFormat.parse("01/28/2025"));

    Submission evePS1 = factory.createSubmission(cs112Homework1.getId(), eve.getId(),
        "This is my PS 1 submission part 1");

    factory.createSubmission(cs112Homework2.getId(), eve.getId(),
        "This is my PS 1 submission part 2");

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
    Instructor cpk = store.get(Instructor.class, "cpk").get();
    System.out.println("Instructor: " + cpk.getName());
    cpk.delete();
    System.out.println(store.get(Student.class, "charlie"));
  }
}
