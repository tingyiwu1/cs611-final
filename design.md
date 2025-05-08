# Design Doc

This project is a course management system built using Java Swing.

---

## Notable Design Patterns

- **Singleton**: The `Store` class is a singleton, ... <!--TODO -->
- **Factory**: The `StoredObjectFactory` class is a factory for creating stored objects.
- 
---

## Files

- `Main.java` : Application entrypoint
- `src.store.Identifiable.java` : Interface for objects that have an ID
- `src.store.StoredObject.java` : Base class for all objects that need to be persisted
- `src.store.Store.java` : Interface for a store that manages the persistence of `StoredObject`s
- `src.store.FileStore.java` : Implementation of `Store` that uses Java serialization to persist objects to a file
- `src.store.StoreExample.java` : Prepopulates the store with example data for testing and demonstration
- `src.model.StoredObjectFactory.java` : Factory class with utility functions for creating stored objects
- `src.model.*` : Persisted data model
- `src.util.*` : Miscellaneous utility classes
- `src.grading.*` : Classes for handling grading and grade calculation
- `src.auth.Auth.java` : Login and user session handling logic
- `src.views.*` : User interface implementation
- `src.views.Navigator.java` : Page navigation system
- `src.views.course.*` : Screens for viewing a course and managing roster and grades.
- `src.views.assignments.*` : Screens for interacting with assignments
- `src.views.editcourse.*` : Screens and components for editing a course's attributes, categories, and graders

## Design Details

### Data Model

### Persistence

### Authentication
- `Auth` class for user authentication and session management
- `LoginPanel` for user login

### Page Navigation
- `Navigator` class for page navigation and panel rendering
- `LoginPanel` for user login
- `CourseListPanel` for course selection and creation
- `CourseViewPanel` for course dashboard
- `GradingPanel` for student grade management
- `StudentRosterFrame` for student roster management
- `AssignmentListPanel` for assignment selection and creation
- `AssignmentViewPanel` for assignment dashboard
- `AssignmentSubmissionPanel` for student submission and plagiarism detection
- `AssignmentGradingPanel` for grader grading and final grade calculation
- `GradeStatisticsPanel` for final grade statistics and histogram visualization
- `EditCategoriesPanel` for editing course categories
- `EditGradersPanel` for editing course graders
- `AssignmentEditorScreen` for creating and editing assignments
- `SubmissionDetailsPanel` for displaying assignment submission details
- `SubmissionsScreen` for displaying a list of assignment submissions
- `StudentAssignmentsScreen` for displaying a list of assignments to students without advanced permissions
- `GraderAssignmentsScreen` for displaying a list of assignments to graders with advanced permissions
- `EditCourseScreen` for editing course attributes, categories, and graders
