package boba.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents a task with a deadline.
 * The deadline can be specified as a date (yyyy-mm-dd format) or as a string.
 */
public class Deadline extends Task {
    protected LocalDate byDate;
    protected String byString;
    protected boolean hasDate;

    /**
     * Creates a new deadline task with the given description and due date.
     * If the date is in yyyy-mm-dd format, it will be parsed as a LocalDate.
     * Otherwise, it will be stored as a plain string.
     *
     * @param description The description of the deadline task.
     * @param by The deadline date/time (preferably in yyyy-mm-dd format).
     */
    public Deadline(String description, String by) {
        super(description);
        try {
            this.byDate = LocalDate.parse(by);
            this.hasDate = true;
            this.byString = by;
        } catch (DateTimeParseException e) {
            this.byString = by;
            this.hasDate = false;
        }
    }

    /**
     * Returns the deadline in a format suitable for storage.
     * If the deadline was parsed as a date, returns it in yyyy-mm-dd format.
     *
     * @return The deadline string for storage.
     */
    public String getByForStorage() {
        if (hasDate) {
            return byDate.toString();
        }
        return byString;
    }

    /**
     * Returns a string representation of this deadline task.
     * If the deadline is a valid date, it is displayed in "MMM d yyyy" format.
     *
     * @return A string in the format "[D][status] description (by: date)".
     */
    @Override
    public String toString() {
        String byDisplay;
        if (hasDate) {
            byDisplay = byDate.format(DateTimeFormatter.ofPattern("MMM d yyyy"));
        } else {
            byDisplay = byString;
        }
        return "[D][" + getStatusIcon() + "] " + description + " (by: " + byDisplay + ")";
    }
}
