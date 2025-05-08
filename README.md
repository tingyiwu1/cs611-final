# CS611 Final Project

---

- Nick Galis / TingYi Wu / Xueyang Xu
- ngalis@bu.edu / tingyiwu@bu.edu / snoun@bu.edu
- <!-- TODO --> / U85278299 / <!-- TODO -->

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
