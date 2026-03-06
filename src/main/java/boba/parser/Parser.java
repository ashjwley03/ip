package boba.parser;

import boba.exception.BobException;
import boba.task.Deadline;
import boba.task.DoAfter;
import boba.task.DoWithin;
import boba.task.Event;
import boba.task.FixedDuration;
import boba.task.Task;
import boba.task.TentativeEvent;
import boba.task.Todo;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Provides utility methods for parsing user input commands.
 */
public class Parser {

    private static final Set<String> VALID_FREQUENCIES =
            Set.of("daily", "weekly", "monthly");

    /**
     * Normalizes whitespace and validates basic input safety.
     */
    private static String sanitize(String args) throws BobException {
        String normalized = args.replaceAll("\\s+", " ").trim();
        if (normalized.contains("|")) {
            throw new BobException(
                    "The '|' character is reserved~\n"
                    + "    Please remove it from your input.");
        }
        return normalized;
    }

    /**
     * Validates that a date string is a real date.
     * Only checks strings that look like yyyy-mm-dd format.
     */
    private static void validateDate(String dateStr, String label)
            throws BobException {
        if (!dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return;
        }
        try {
            LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new BobException("'" + dateStr
                    + "' is not a valid date for " + label + "~\n"
                    + "    Check the month/day values.");
        }
    }

    /**
     * Validates that start date is before end date when both
     * are in yyyy-mm-dd format.
     */
    private static void validateDateOrder(
            String fromStr, String toStr,
            String fromLabel, String toLabel)
            throws BobException {
        if (!fromStr.matches("\\d{4}-\\d{2}-\\d{2}")
                || !toStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return;
        }
        LocalDate from = LocalDate.parse(fromStr);
        LocalDate to = LocalDate.parse(toStr);
        if (from.isAfter(to)) {
            throw new BobException(fromLabel + " (" + fromStr
                    + ") is after " + toLabel + " (" + toStr
                    + ")~\n    That doesn't seem right!");
        }
    }

    private static void rejectDuplicateFlag(
            String args, String flag) throws BobException {
        int first = args.indexOf(flag);
        if (first >= 0 && args.indexOf(flag, first + 1) >= 0) {
            throw new BobException(
                    "'" + flag.trim() + "' appears more than "
                    + "once~\n    Each flag should only be "
                    + "used once.");
        }
    }

    /**
     * Extracts a "/every frequency" suffix from args if present.
     * Returns a 2-element array: [argsWithoutEvery, frequency].
     * If no /every is found, frequency is null.
     */
    private static String[] extractRecurrence(String args)
            throws BobException {
        if (!args.contains(" /every ")) {
            return new String[]{args, null};
        }
        int idx = args.lastIndexOf(" /every ");
        String before = args.substring(0, idx).trim();
        String freq = args.substring(idx + 8).trim().toLowerCase();
        if (!VALID_FREQUENCIES.contains(freq)) {
            throw new BobException(
                    "Invalid frequency '" + freq + "'~\n"
                    + "    Use: daily, weekly, or monthly");
        }
        return new String[]{before, freq};
    }

    /**
     * Applies the parsed recurrence to a task, if any.
     */
    private static <T extends Task> T applyRecurrence(
            T task, String frequency) {
        if (frequency != null) {
            task.setRecurrence(frequency);
        }
        return task;
    }

    /**
     * Extracts the command word from user input.
     *
     * @param input The full user input string.
     * @return The first word of the input (the command).
     */
    public static String getCommand(String input) {
        assert input != null : "Input should not be null";
        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        return trimmed.split("\\s+")[0].toLowerCase();
    }

    /**
     * Extracts the arguments from user input (everything after the command).
     * Normalizes multiple spaces into single spaces.
     *
     * @param input The full user input string.
     * @return The arguments portion of the input, or empty string if none.
     */
    public static String getArguments(String input) {
        String trimmed = input.trim().replaceAll("\\s+", " ");
        int spaceIndex = trimmed.indexOf(" ");
        if (spaceIndex == -1) {
            return "";
        }
        return trimmed.substring(spaceIndex + 1).trim();
    }

    /**
     * Parses a task index from user input.
     * Converts from 1-based user input to 0-based array index.
     *
     * @param input The full user input string containing the index.
     * @return The 0-based index.
     * @throws NumberFormatException If the index is not a valid number.
     */
    public static int parseIndex(String input) throws NumberFormatException {
        String[] parts = input.trim().split("\\s+");
        if (parts.length < 2 || parts[1].isEmpty()) {
            throw new NumberFormatException("No index provided");
        }
        int index = Integer.parseInt(parts[1]);
        if (index <= 0) {
            throw new NumberFormatException(
                    "Index must be a positive number");
        }
        return index - 1;
    }

    /**
     * Parses arguments to create a Todo task.
     *
     * @param args The task description.
     * @return A new Todo task.
     * @throws BobException If the description is empty.
     */
    public static Todo parseTodo(String args) throws BobException {
        String clean = sanitize(args);
        if (clean.isEmpty()) {
            throw new BobException(
                    "Uhh what's the task? Can't be empty~\n"
                    + "    Try: todo <description>");
        }
        String[] rec = extractRecurrence(clean);
        return applyRecurrence(new Todo(rec[0]), rec[1]);
    }

    /**
     * Parses arguments to create a Deadline task.
     * Expected format: "description /by deadline"
     *
     * @param args The arguments containing description and deadline.
     * @return A new Deadline task.
     * @throws BobException If the format is invalid or fields are missing.
     */
    public static Deadline parseDeadline(String args) throws BobException {
        String clean = sanitize(args);
        String[] rec = extractRecurrence(clean);
        String cleaned = rec[0];
        if (!cleaned.contains(" /by ")) {
            throw new BobException("When's it due? Add /by <date>~\n"
                    + "    Try: deadline <description> /by <when>");
        }
        rejectDuplicateFlag(cleaned, " /by ");
        String[] parts = cleaned.split(" /by ", 2);
        String description = parts[0].trim();
        String by = parts[1].trim();
        if (description.isEmpty() || by.isEmpty()) {
            throw new BobException("Hmm something's missing there~\n"
                    + "    Try: deadline <description> /by <when>");
        }
        validateDate(by, "/by");
        return applyRecurrence(new Deadline(description, by), rec[1]);
    }

    /**
     * Parses arguments to create an Event task.
     * Expected format: "description /from start /to end"
     *
     * @param args The arguments containing description and time range.
     * @return A new Event task.
     * @throws BobException If the format is invalid or fields are missing.
     */
    public static Event parseEvent(String args) throws BobException {
        String clean = sanitize(args);
        String[] rec = extractRecurrence(clean);
        String cleaned = rec[0];
        if (!cleaned.contains(" /from ") || !cleaned.contains(" /to ")) {
            throw new BobException("I need both /from and /to times~\n"
                    + "    Try: event <description> /from <start>"
                    + " /to <end>");
        }
        rejectDuplicateFlag(cleaned, " /from ");
        rejectDuplicateFlag(cleaned, " /to ");
        String[] parts = cleaned.split(" /from ", 2);
        String description = parts[0].trim();
        String[] timeParts = parts[1].split(" /to ", 2);
        String from = timeParts[0].trim();
        String to = timeParts[1].trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new BobException("Hmm something's missing there~\n"
                    + "    Try: event <description> /from <start>"
                    + " /to <end>");
        }
        validateDate(from, "/from");
        validateDate(to, "/to");
        validateDateOrder(from, to, "/from", "/to");
        return applyRecurrence(new Event(description, from, to), rec[1]);
    }

    /**
     * Parses arguments to create a DoAfter task.
     * Expected format: "description /after condition"
     *
     * @param args The arguments containing description and condition.
     * @return A new DoAfter task.
     * @throws BobException If the format is invalid or fields are missing.
     */
    public static DoAfter parseDoAfter(String args) throws BobException {
        String clean = sanitize(args);
        String[] rec = extractRecurrence(clean);
        String cleaned = rec[0];
        if (!cleaned.contains(" /after ")) {
            throw new BobException(
                    "What should this be done after?~\n"
                    + "    Try: doafter <description>"
                    + " /after <time or task>");
        }
        String[] parts = cleaned.split(" /after ", 2);
        String description = parts[0].trim();
        String after = parts[1].trim();
        if (description.isEmpty() || after.isEmpty()) {
            throw new BobException(
                    "Hmm something's missing there~\n"
                    + "    Try: doafter <description>"
                    + " /after <time or task>");
        }
        return applyRecurrence(
                new DoAfter(description, after), rec[1]);
    }

    /**
     * Parses arguments to create a DoWithin task.
     * Expected format: "description /between start /and end"
     *
     * @param args The arguments containing description and period.
     * @return A new DoWithin task.
     * @throws BobException If the format is invalid or fields missing.
     */
    public static DoWithin parseDoWithin(String args)
            throws BobException {
        String clean = sanitize(args);
        String[] rec = extractRecurrence(clean);
        String cleaned = rec[0];
        if (!cleaned.contains(" /between ") || !cleaned.contains(" /and ")) {
            throw new BobException(
                    "I need both /between and /and~\n"
                    + "    Try: dowithin <description>"
                    + " /between <start> /and <end>");
        }
        rejectDuplicateFlag(cleaned, " /between ");
        rejectDuplicateFlag(cleaned, " /and ");
        String[] parts = cleaned.split(" /between ", 2);
        String description = parts[0].trim();
        String[] periodParts = parts[1].split(" /and ", 2);
        String start = periodParts[0].trim();
        String end = periodParts[1].trim();
        if (description.isEmpty() || start.isEmpty()
                || end.isEmpty()) {
            throw new BobException(
                    "Hmm something's missing there~\n"
                    + "    Try: dowithin <description>"
                    + " /between <start> /and <end>");
        }
        validateDate(start, "/between");
        validateDate(end, "/and");
        validateDateOrder(start, end, "/between", "/and");
        return applyRecurrence(
                new DoWithin(description, start, end), rec[1]);
    }

    /**
     * Parses arguments to create a FixedDuration task.
     * Expected format: "description /needs duration"
     *
     * @param args The arguments containing description and duration.
     * @return A new FixedDuration task.
     * @throws BobException If the format is invalid or fields missing.
     */
    public static FixedDuration parseFixedDuration(String args)
            throws BobException {
        String clean = sanitize(args);
        String[] rec = extractRecurrence(clean);
        String cleaned = rec[0];
        if (!cleaned.contains(" /needs ")) {
            throw new BobException(
                    "How long does this task take?~\n"
                    + "    Try: fixed <description>"
                    + " /needs <duration>");
        }
        String[] parts = cleaned.split(" /needs ", 2);
        String description = parts[0].trim();
        String duration = parts[1].trim();
        if (description.isEmpty() || duration.isEmpty()) {
            throw new BobException(
                    "Hmm something's missing there~\n"
                    + "    Try: fixed <description>"
                    + " /needs <duration>");
        }
        return applyRecurrence(
                new FixedDuration(description, duration), rec[1]);
    }

    /**
     * Parses a snooze command for a Deadline task.
     * Expected format: "<taskIndex> /to <newDate>"
     *
     * @param args The arguments after the snooze command.
     * @return A String array of [taskIndex (0-based as string), newDate].
     * @throws BobException If the format is invalid.
     */
    public static String[] parseSnoozeDeadline(String args)
            throws BobException {
        String clean = sanitize(args);
        if (!clean.contains(" /to ")) {
            throw new BobException(
                    "When should I reschedule it to?~\n"
                    + "    Deadline: snooze <task#> /to <new date>\n"
                    + "    Event: snooze <task#> /from <start>"
                    + " /to <end>");
        }
        String[] parts = clean.split(" /to ", 2);
        String indexStr = parts[0].trim();
        String newDate = parts[1].trim();
        if (newDate.isEmpty()) {
            throw new BobException("The new date can't be empty~");
        }
        validateDate(newDate, "/to");
        try {
            int index = Integer.parseInt(indexStr) - 1;
            if (index < 0) {
                throw new BobException(
                        "Task number must be positive~");
            }
            return new String[]{String.valueOf(index), newDate};
        } catch (NumberFormatException e) {
            throw new BobException("That's not a valid task number~\n"
                    + "    Try: snooze <task#> /to <new date>");
        }
    }

    /**
     * Parses a snooze command for an Event task.
     * Expected format: "<taskIndex> /from <newFrom> /to <newTo>"
     *
     * @param args The arguments after the snooze command.
     * @return A String array of [taskIndex (0-based as string), from, to].
     * @throws BobException If the format is invalid.
     */
    public static String[] parseSnoozeEvent(String args)
            throws BobException {
        String clean = sanitize(args);
        if (!clean.contains(" /from ") || !clean.contains(" /to ")) {
            throw new BobException(
                    "Both /from and /to are needed~\n"
                    + "    Try: snooze <task#> /from <start>"
                    + " /to <end>");
        }
        String[] parts = clean.split(" /from ", 2);
        String indexStr = parts[0].trim();
        String[] timeParts = parts[1].split(" /to ", 2);
        String newFrom = timeParts[0].trim();
        String newTo = timeParts[1].trim();
        if (newFrom.isEmpty() || newTo.isEmpty()) {
            throw new BobException(
                    "Both /from and /to are needed~\n"
                    + "    Try: snooze <task#> /from <start>"
                    + " /to <end>");
        }
        validateDate(newFrom, "/from");
        validateDate(newTo, "/to");
        validateDateOrder(newFrom, newTo, "/from", "/to");
        try {
            int index = Integer.parseInt(indexStr) - 1;
            if (index < 0) {
                throw new BobException(
                        "Task number must be positive~");
            }
            return new String[]{String.valueOf(index), newFrom, newTo};
        } catch (NumberFormatException e) {
            throw new BobException("That's not a valid task number~\n"
                    + "    Try: snooze <task#> /from <start>"
                    + " /to <end>");
        }
    }

    /**
     * Parses arguments to create a TentativeEvent.
     * Expected format: "description /slot from1 - to1 /slot from2 - to2 ..."
     *
     * @param args The arguments containing description and time slots.
     * @return A new TentativeEvent.
     * @throws BobException If the format is invalid or fewer than 2 slots.
     */
    public static TentativeEvent parseTentative(String args) throws BobException {
        String clean = sanitize(args);
        if (!clean.contains(" /slot ")) {
            throw new BobException("I need time slots for tentative events~\n"
                    + "    Try: tentative <description> /slot <from> - <to>"
                    + " /slot <from> - <to>");
        }
        String[] parts = clean.split(" /slot ");
        String description = parts[0].trim();
        if (description.isEmpty()) {
            throw new BobException("What's the event called?~\n"
                    + "    Try: tentative <description> /slot ...");
        }

        List<String[]> slots = new ArrayList<>();
        for (int i = 1; i < parts.length; i++) {
            String slotStr = parts[i].trim();
            if (!slotStr.contains(" - ")) {
                throw new BobException("Each slot needs a"
                        + " 'from - to' format~\n"
                        + "    e.g. /slot Mon 2pm - 4pm");
            }
            String[] timeParts = slotStr.split(" - ", 2);
            String slotFrom = timeParts[0].trim();
            String slotTo = timeParts[1].trim();
            if (slotFrom.isEmpty() || slotTo.isEmpty()) {
                throw new BobException("Slot " + i
                        + " has empty from/to~");
            }
            slots.add(new String[]{slotFrom, slotTo});
        }

        if (slots.size() < 2) {
            throw new BobException("Need at least 2 time slots for"
                    + " tentative scheduling~\n"
                    + "    Try adding more /slot entries!");
        }
        return new TentativeEvent(description, slots);
    }

    /**
     * Parses a confirm command to extract task index and slot number.
     * Expected format: "confirm <taskIndex> /slot <slotNumber>"
     *
     * @param args The arguments after the confirm command.
     * @return An int array of [taskIndex (0-based), slotIndex (0-based)].
     * @throws BobException If the format is invalid.
     */
    public static int[] parseConfirm(String args) throws BobException {
        String clean = sanitize(args);
        if (!clean.contains(" /slot ")) {
            throw new BobException("Which slot to confirm?~\n"
                    + "    Try: confirm <task#> /slot <slot#>");
        }
        String[] parts = clean.split(" /slot ");
        try {
            int taskIndex = Integer.parseInt(parts[0].trim()) - 1;
            int slotIndex = Integer.parseInt(parts[1].trim()) - 1;
            return new int[]{taskIndex, slotIndex};
        } catch (NumberFormatException e) {
            throw new BobException("Those don't look like numbers~\n"
                    + "    Try: confirm <task#> /slot <slot#>");
        }
    }
}
