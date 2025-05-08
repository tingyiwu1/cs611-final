# Design Doc

This project is a course management system built using Java Swing.

---

## Notable Design Patterns

Composite `Process`:

- Process is an abstract class that defines a `run()` method. All processes implement this `run` method based on their required activity
- `Process.run()` recursively calls other processes based on user input. e.g. `HeroTurnProcess` calls `inventoryProcess` or `battleProcess`
- The game runs through recursively calling processes until game end or user quit

Facade `Main`

- Main.java simply starts the main process, which reduces complexity from initialization

Singleton `StatsTracker`

- Allows any part of the code to increment arbitrary statistics that are displayed to the user at the end.

`InputProcess.Option` parsing Strategy

- Client passes parsing function to each `Option` declared when constructing an `InputProcess`
- `InputProcess` calls the parsing function on each `Option` to decide which one the user chose

Monster/Item/Market Factory

- The creation of Monsters is based off of the `BattleMonsterFactory` class which exposes static method `generateRandomMonster()`, pulling a monster from a list of options from a text file
- This hides the complexity of Monster Creation from the client, reducing complexity, while also allowing for random creation of monsters for battle.
- `ItemFactory` takes care of generating a set of random MarketItems whenever a market needs to be refreshed.
- `MarketFactory` creating and maintaining `Market` instances that correspond to each `Hero` to allow each hero to access it's own `Market` instance. Also handles creating new `Market`s, allowing items to refresh when a hero buys out all the items or the hero levels up.
  - Hides complexity of maintaining different instances, reducing the client code to a simple `getMarket()` call.

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

### Data Model

### Persistence

### Authentication

### Page Navigation
<!-- TODO -->
