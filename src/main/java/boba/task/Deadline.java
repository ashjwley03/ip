package boba.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

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
     * Reschedules this deadline to a new date/time.
     *
     * @param newBy The new deadline date/time.
     */
    public void reschedule(String newBy) {
        try {
            this.byDate = LocalDate.parse(newBy);
            this.hasDate = true;
            this.byString = newBy;
        } catch (DateTimeParseException e) {
            this.byString = newBy;
            this.hasDate = false;
            this.byDate = null;
        }
    }

    /**
     * Returns whether this deadline has a parseable date.
     *
     * @return True if the deadline has a LocalDate.
     */
    public boolean hasDate() {
        return hasDate;
    }

    /**
     * Returns the deadline date, or null if not parseable.
     *
     * @return The LocalDate, or null.
     */
    public LocalDate getByDate() {
        return byDate;
    }

    /**
     * Creates a new Deadline advanced by the given recurrence period.
     *
     * @param frequency The recurrence (daily/weekly/monthly).
     * @return A new Deadline with the advanced date and same recurrence.
     */
    public Deadline createNextOccurrence(String frequency) {
        String nextBy;
        if (hasDate) {
            LocalDate next = advanceDate(byDate, frequency);
            nextBy = next.toString();
        } else {
            nextBy = byString;
        }
        Deadline d = new Deadline(description, nextBy);
        d.setRecurrence(frequency);
        return d;
    }

    private static LocalDate advanceDate(LocalDate date, String freq) {
        switch (freq) {
        case "daily":
            return date.plus(1, ChronoUnit.DAYS);
        case "weekly":
            return date.plus(1, ChronoUnit.WEEKS);
        case "monthly":
            return date.plus(1, ChronoUnit.MONTHS);
        default:
            return date;
        }
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
        return "[D][" + getStatusIcon() + "] " + getRecurrenceTag()
                + description + " (by: " + byDisplay + ")";
    }
}
