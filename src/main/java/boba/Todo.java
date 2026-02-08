package boba;

/**
 * Represents a todo task without any date/time attached.
 */
public class Todo extends Task {

    /**
     * Creates a new todo task with the given description.
     *
     * @param description The description of the todo.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns a string representation of this todo task.
     *
     * @return A string in the format "[T][status] description".
     */
    @Override
    public String toString() {
        return "[T][" + getStatusIcon() + "] " + description;
    }
}
