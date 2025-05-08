# CS611 Final Project

---

- Nick Galis / TingYi Wu / Xueyang Xu
- ngalis@bu.edu / tingyiwu@bu.edu / snoun@bu.edu
- <!-- TODO --> / U85278299 / U38282683

---

## Notable Files / Directories

- `Main.java` : Application entrypoint
- `src.store` : Infrastructure for handling data persistence using object serialization
- `src.model` : Persisted data model
- `src.auth` : Login and user session handling logic
- `src.util` : Miscellaneous utility classes
- `src.views` : User interface implementation
- `src.views.Navigator.java` : Page navigation system

---

## Package Overview

### `grading` package  
Implements grading logic with multiple interchangeable strategies using the Strategy Pattern.

**Classes:**
- `GradeCalculator`: Core engine that handles student grade calculation and strategy delegation.  
- `GradingStrategy`: Interface for grade computation algorithms.  
- `RatioStrategy`: Standard weighted average based on assignment weights.  
- `DropLowNStrategy`: Drops the lowest N assignment scores before averaging.  
- `CategoryRatioStrategy`: Aggregates scores by category, then applies normalized weighting.  
- `PlagiarismChecker`: Detects similar submissions to identify potential plagiarism cases.  

---

### `views` package  
Handles the entire Swing-based user interface, with panels for login, course navigation, grading, and visualization.

**Classes:**
- `MainWindow`: Top-level `JFrame` managing login and post-login views with panel switching.  
- `Navigator`: Utility class that manages screen navigation with back-stack and dynamic panel rendering.  
- `LoginPanel`: User login interface with ID input and feedback.  
- `CourseListPanel`: Displays all courses grouped by semester and allows course creation.  
- `CourseViewPanel`: Dashboard view that adapts available actions based on user roles (Instructor, Grader, Student).  
- `GradingPanel`: Table and histogram view for instructors to manage and analyze student submissions.  
- `GradeStatisticsPanel`: Shows final grade statistics (min, max, avg) and renders a histogram.  
- `SetGradingRulePanel`: Provides UI to configure grading strategies (ratio-based or drop-low).  
- `StudentRosterFrame`: Interface for viewing and modifying student enrollments in a course.  

---

## Notes

Implemented a course management system using Java Swing, with a focus on Object-Oriented design. The featureset is focused on the perspective of a course admin.

Features include:

- Basic user login system
- Course creation and management
- Management of enrolled students, graders, assignments, and grades
- Ability to clone all of a course's assignments and weighted grading to a new term
- Plagiarism detection of assignment submissions
- Automatic final grade and letter grade calculation based on weighted categories
- Histogram visualization of final grades
<!-- TODO: anything i missed? -->

#### Design highlights, covered more in design.md:

- Data persistence using Java serialization
- Robust handling of relations between stored objects
- Hierarchical class structure screens and components
<!-- TODO: add any additional features or notes here -->

---

## How to Compile and Run

From the source directory:

- Compile the code using: `mkdir out && javac -d out $(find . -name "*.java")`
- Run the program: `java -cp out Main`
- See file structure if gradescope messes it up:
  - [https://github.com/tingyiwu1/cs611-final/](https://github.com/tingyiwu1/cs611-final/)

---

## UML Diagram

<!-- TODO: embed UML here or say which file it is -->

---

## Dependencies and Requirements

- Tested on Java 1.8.345

---

## Testing Strategy

<!-- TODO -->

---

## Known Issues

<!-- TODO -->

---

## References and Attribution

- Used Stack Overflow for troubleshooting file I/O issues.
- Used ChatGPT for debugging assistance.
- Used GitHub Copilot for boilerplate generation and autocomplete while coding.
