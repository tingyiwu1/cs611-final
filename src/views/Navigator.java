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
    private String current;

    public Navigator(JPanel container, CardLayout layout) {
        this.container = container;
        this.layout    = layout;
    }

    /**
     * Register a screen under a key, using a Supplier to build it on demand.
     * The lambda () -> new MyPanel(...) will be assigned here.
     */
    public void register(String key, Supplier<JPanel> supplier) {
        suppliers.put(key, supplier);
    }

    /**
     * Push a screen by key. Builds a fresh panel via the Supplier each time.
     */
    public void push(String key) {
        Supplier<JPanel> sup = suppliers.get(key);
        if (sup == null) {
            throw new IllegalArgumentException("No screen registered for key: " + key);
        }
        if (current != null) {
            history.push(current);
        }

        // Remove any old instance
        JPanel old = screens.remove(key);
        if (old != null) {
            container.remove(old);
        }

        // Build & add new
        JPanel panel = sup.get();
        screens.put(key, panel);
        container.add(panel, key);

        current = key;
        layout.show(container, key);
    }

    /** Pop back to the previous screen (if any). */
    public void back() {
        if (history.isEmpty()) return;
        current = history.pop();
        layout.show(container, current);
    }
}
