package boba;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Deadline extends Task {
    protected LocalDate byDate;
    protected String byString;
    protected boolean hasDate;

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

    public String getByForStorage() {
        if (hasDate) {
            return byDate.toString();
        }
        return byString;
    }

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
