package boba.task;

/**
 * Represents a task that should be done after a specific time or condition.
 * e.g., "return book" after "the exam is over"
 */
public class DoAfter extends Task {
    protected String after;

    /**
     * Creates a new do-after task.
     *
     * @param description The description of the task.
     * @param after The condition/time after which this task should be done.
     */
    public DoAfter(String description, String after) {
        super(description);
        this.after = after;
    }

    /**
     * Returns the after-condition for this task.
     *
     * @return The after-condition string.
     */
    public String getAfter() {
        return after;
    }

    @Override
    public String toString() {
        return "[DA][" + getStatusIcon() + "] " + getRecurrenceTag()
                + description + " (after: " + after + ")";
    }
}
