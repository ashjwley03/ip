package boba.task;

/**
 * Represents a task with a description and completion status.
 * This is the base class for all task types (Todo, Deadline, Event).
 */
public class Task {
    protected String description;
    protected boolean isDone;
    protected String recurrence;

    /**
     * Creates a new task with the given description.
     * The task is initially marked as not done.
     *
     * @param description The description of the task.
     */
    public Task(String description) {
        assert description != null : "Task description should not be null";
        assert !description.trim().isEmpty() : "Task description should not be empty";
        this.description = description;
        this.isDone = false;
        this.recurrence = null;
    }

    /**
     * Returns the status icon for this task.
     *
     * @return "X" if the task is done, " " (space) otherwise.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns whether this task is done.
     *
     * @return True if task is done, false otherwise.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the description of this task.
     *
     * @return The task description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the recurrence frequency, or null if not recurring.
     *
     * @return The recurrence string (daily/weekly/monthly), or null.
     */
    public String getRecurrence() {
        return recurrence;
    }

    /**
     * Sets the recurrence frequency.
     *
     * @param recurrence The frequency (daily/weekly/monthly), or null.
     */
    public void setRecurrence(String recurrence) {
        this.recurrence = recurrence;
    }

    /**
     * Returns whether this task is recurring.
     *
     * @return True if the task has a recurrence set.
     */
    public boolean isRecurring() {
        return recurrence != null;
    }

    /**
     * Returns a tag showing the recurrence, or empty if not recurring.
     *
     * @return A string like "[R:weekly]" or empty string.
     */
    protected String getRecurrenceTag() {
        if (recurrence == null) {
            return "";
        }
        return "[R:" + recurrence + "] ";
    }

    /**
     * Returns a string representation of this task.
     *
     * @return A string showing the task's status and description.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + getRecurrenceTag()
                + description;
    }
}
