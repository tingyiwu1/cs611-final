# Design Doc

This project is a course management system built using Java Swing.

---

## Notable Design Patterns

- **Facade/Singleton**: The `MainWindow` class is a singleton which represents the application itself. It also acts as a facade for the `Store` and `Auth` objects, making them effectively singletons as well, as only one instance of each is created and used throughout the application lifecycle. This aligns with the purpose of these classes: the application has exactly one external source of truth and exactly one active user session at a time.

- **Proxy**: The `StoredObject.ForeignKey` class serves as a proxy for accessing related `StoredObject`s. It stores the ID of the related object and provides methods for retrieving the object from the store. This allows us to avoid holding a direct reference to the related object, which would cause Java to serialize the entire object graph. Caching isn't implemented, but could be easily added here if performance becomes a concern. The `StoredObject.ForeignSet` class serves a similar purpose for collections of related objects.

- **Composite**: The deletion behavior of `StoredObject`s is handled using the Composite pattern. When the `delete()` method on `StoredObject` is called, it calls the `delete()` method on all child `StoredObject`s referenced through `ForeignSet`s. In this way, `StoredObject` is treated as both a leaf object to be deleted and a composite object that contains and deletes child `StoredObject`s.

- **Factory**: The `StoredObjectFactory` class provides utility functions for creating `StoredObject`s. This is especially useful because every `StoredObject` instance must have a corresponding `Store` instance to back its persistence. The `StoredObjectFactory` class provides a convenient way to create `StoredObject`s without having to pass the `Store` instance around everywhere.

- **Strategy**: The `GradingStrategy` interface and its implementations (`RatioStrategy`, `DropLowNStrategy`, `CategoryRatioStrategy`) allow for different grading strategies to be used interchangeably. The `GradeCalculator` class delegates the actual grade calculation to the selected strategy.

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
<!-- TODO: anything I missed? -->

## Design Details
The project involves a graphical user interface (GUI) built using Java Swing, and a persistent data store using Java serialization.

### Persistence

The `Store` interface defines the contract for a store (see `Store.java` for detailed comments) that manages the persistence of `StoredObject`s, which is the base class for all objects that need to be persisted. `FileStore` is an implementation of the `Store` interface that uses Java serialization to persist objects to a file. The `FileStore` class provides methods for loading and saving the store's contents to a file, encapsulating all the file I/O logic as well as handling serialization and deserialization specifics like populating the transient `StoredObject.store` field (See comments in `FileStore.java` for details).

The relations between `StoredObject`s are handled similarly to as is done in traditional relational databases: we serialize the id of the related object as a field in the object. Although Java does support directly serializing references to other objects, we start to run into issues when deleting objects in the store that are referenced by other objects, as an object has no way of knowing if it is being referenced by another object in order to notify the other object to remove the reference.

This is handled by the `ForeignKey` and `ForeignSet` classes, which are used to store references to other `StoredObject`s (comments in `StoredObject.java` outline exactly how this is done in detail). The `ForeignKey` class stores the ID of the related object and provides methods for retrieving the object from the store. The `ForeignSet` class represents a collection of related objects which have a `ForeignKey` to the original object (we use Java's reflection to verify this when the class is loaded), and implements iterating over the collection by filtering objects of the correct type from the store (Caching and/or indices could be easily added here if performance becomes a concern).

### Data Model

We declare a set of `StoredObject` subclasses in `src.model` that represent the data model of the application. The files for these classes are designed to be declarative and simple to read/understand by following a consistent structure (similar to a database schema), so I definitely recommend looking at these files directly.

In each `StoredObject` subclass, we include associated helper methods for reading and updating related objects. For example, the `Course` class has methods for adding and removing students, and the `Assignment` class has methods for adding and removing submissions. This allows us to encapsulate the logic for managing related objects within the `StoredObject` subclasses themselves, and makes the `StoredObject`s' API extremely ergonomic to use.

### Authentication
User sessions are handled by the `Auth` class, which provides methods for logging in and out, as well as convenience methods for checking the current user's role. Our login system is very simple, with user id being the only credential required. If a more robust login system is desired, we could easily implement it here.

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
