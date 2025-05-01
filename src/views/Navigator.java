package views;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.function.Supplier;

/**
 * A simple CardLayout navigator with a back-stack.
 */
public class Navigator {
    private final JPanel container;
    private final CardLayout layout;
    private final Deque<String> history = new ArrayDeque<>();
    private final Map<String, Supplier<JPanel>> suppliers = new HashMap<>();
    private final Map<String, JPanel> screens = new HashMap<>();

    public Navigator(JPanel container, CardLayout layout) {
        this.container = container;
        this.layout = layout;
    }

    /**
     * Register a screen under a key, using a Supplier to build it on demand.
     * The lambda () -> new MyPanel(...) will be assigned here.
     */
    public void register(String key, Supplier<JPanel> supplier) {
        suppliers.put(key, supplier);
    }

    private void render() {
        String key = history.peek();
        if (key == null) {
            throw new IllegalStateException("No current screen to render");
        }

        Supplier<JPanel> sup = suppliers.get(key);
        assert sup != null : "render called without a registered screen:" + key;

        // Remove any old instance
        JPanel old = screens.remove(key);
        if (old != null) {
            container.remove(old);
        }

        // Build & add new
        JPanel panel = sup.get();
        screens.put(key, panel);
        container.add(panel, key);

        layout.show(container, key);
    }

    /**
     * Push a screen by key. Builds a fresh panel via the Supplier each time.
     */
    public void push(String key) {
        if (suppliers.get(key) == null) {
            throw new IllegalArgumentException("No screen registered for key: " + key);
        }
        history.push(key);

        render();
    }

    /**
     * Open a new screen by key, replacing the current history entry.
     */
    public void replace(String key) {
        if (suppliers.get(key) == null) {
            throw new IllegalArgumentException("No screen registered for key: " + key);
        }
        if (history.isEmpty()) {
            throw new IllegalStateException("No current screen to replace");
        }
        history.pop();
        history.push(key);
        render();
    }

    /** Pop back to the previous screen (if any). */
    public void back() {
        if (history.size() < 2) {
            System.out.println("No previous screen to go back to");
            return;
        }
        history.pop();
        render();
    }

    /**
     * Goes back to a specific screen in the history.
     * 
     * This will pop all screens until the specified key is found. If the key is not
     * in history, an exception is thrown and the history is unchanged.
     */
    public void backTo(String key) {
        if (!history.contains(key)) {
            throw new NoSuchElementException(key);
        }
        while (!history.peek().equals(key)) {
            history.pop();
        }
        render();
    }
}
