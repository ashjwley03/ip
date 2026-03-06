package boba.task;

/**
 * Represents a task that needs to be done within a specific period.
 * e.g., "collect certificate" between "Jan 15" and "Jan 25"
 */
public class DoWithin extends Task {
    protected String start;
    protected String end;

    /**
     * Creates a new do-within-period task.
     *
     * @param description The description of the task.
     * @param start The start of the period.
     * @param end The end of the period.
     */
    public DoWithin(String description, String start, String end) {
        super(description);
        this.start = start;
        this.end = end;
    }

    /**
     * Returns the start of the period.
     *
     * @return The start string.
     */
    public String getStart() {
        return start;
    }

    /**
     * Returns the end of the period.
     *
     * @return The end string.
     */
    public String getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return "[DW][" + getStatusIcon() + "] " + getRecurrenceTag()
                + description + " (between: " + start + " and: "
                + end + ")";
    }
}
