package boba.task;

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
     * Creates a new Todo with the same description and recurrence.
     *
     * @param frequency The recurrence (daily/weekly/monthly).
     * @return A new undone Todo with the same recurrence.
     */
    public Todo createNextOccurrence(String frequency) {
        Todo t = new Todo(description);
        t.setRecurrence(frequency);
        return t;
    }

    /**
     * Returns a string representation of this todo task.
     *
     * @return A string in the format "[T][status] description".
     */
    @Override
    public String toString() {
        return "[T][" + getStatusIcon() + "] " + getRecurrenceTag()
                + description;
    }
}
