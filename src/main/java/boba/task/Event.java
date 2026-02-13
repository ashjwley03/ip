package boba.task;

/**
 * Represents an event task with a start time and end time.
 */
public class Event extends Task {
    protected String from;
    protected String to;

    /**
     * Creates a new event task with the given description and time range.
     *
     * @param description The description of the event.
     * @param from The start time of the event.
     * @param to The end time of the event.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the start time of this event.
     *
     * @return The start time.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the end time of this event.
     *
     * @return The end time.
     */
    public String getTo() {
        return to;
    }

    /**
     * Returns a string representation of this event task.
     *
     * @return A string in the format "[E][status] description (from: start to: end)".
     */
    @Override
    public String toString() {
        return "[E][" + getStatusIcon() + "] " + description + " (from: " + from + " to: " + to + ")";
    }
}
