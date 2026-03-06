package boba.task;

/**
 * Represents a task that takes a fixed amount of time but has no
 * fixed start/end time. e.g., "read sales report" needs 2 hours.
 */
public class FixedDuration extends Task {
    protected String duration;

    /**
     * Creates a new fixed-duration task.
     *
     * @param description The description of the task.
     * @param duration The duration needed (e.g., "2 hours").
     */
    public FixedDuration(String description, String duration) {
        super(description);
        this.duration = duration;
    }

    /**
     * Returns the duration of this task.
     *
     * @return The duration string.
     */
    public String getDuration() {
        return duration;
    }

    @Override
    public String toString() {
        return "[FD][" + getStatusIcon() + "] " + getRecurrenceTag()
                + description + " (needs: " + duration + ")";
    }
}
