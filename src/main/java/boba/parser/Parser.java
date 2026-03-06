package boba.parser;

import boba.exception.BobException;
import boba.task.Deadline;
import boba.task.DoAfter;
import boba.task.Event;
import boba.task.Task;
import boba.task.TentativeEvent;
import boba.task.Todo;

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
        assert !input.trim().isEmpty() : "Input should not be empty";
        return input.split(" ")[0];
    }

    /**
     * Extracts the arguments from user input (everything after the command).
     *
     * @param input The full user input string.
     * @return The arguments portion of the input, or empty string if none.
     */
    public static String getArguments(String input) {
        int spaceIndex = input.indexOf(" ");
        if (spaceIndex == -1) {
            return "";
        }
        return input.substring(spaceIndex + 1).trim();
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
        String[] parts = input.split(" ");
        if (parts.length < 2) {
            throw new NumberFormatException("No index provided");
        }
        return Integer.parseInt(parts[1]) - 1;
    }

    /**
     * Parses arguments to create a Todo task.
     *
     * @param args The task description.
     * @return A new Todo task.
     * @throws BobException If the description is empty.
     */
    public static Todo parseTodo(String args) throws BobException {
        if (args.isEmpty()) {
            throw new BobException(
                    "Uhh what's the task? Can't be empty~\n"
                    + "    Try: todo <description>");
        }
        String[] rec = extractRecurrence(args);
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
        String[] rec = extractRecurrence(args);
        String cleaned = rec[0];
        if (!cleaned.contains(" /by ")) {
            throw new BobException("When's it due? Add /by <date>~\n"
                    + "    Try: deadline <description> /by <when>");
        }
        String[] parts = cleaned.split(" /by ");
        String description = parts[0].trim();
        String by = parts[1].trim();
        if (description.isEmpty() || by.isEmpty()) {
            throw new BobException("Hmm something's missing there~\n"
                    + "    Try: deadline <description> /by <when>");
        }
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
        String[] rec = extractRecurrence(args);
        String cleaned = rec[0];
        if (!cleaned.contains(" /from ") || !cleaned.contains(" /to ")) {
            throw new BobException("I need both /from and /to times~\n"
                    + "    Try: event <description> /from <start>"
                    + " /to <end>");
        }
        String[] parts = cleaned.split(" /from ");
        String description = parts[0].trim();
        String[] timeParts = parts[1].split(" /to ");
        String from = timeParts[0].trim();
        String to = timeParts[1].trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new BobException("Hmm something's missing there~\n"
                    + "    Try: event <description> /from <start>"
                    + " /to <end>");
        }
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
        String[] rec = extractRecurrence(args);
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
     * Parses a snooze command for a Deadline task.
     * Expected format: "<taskIndex> /to <newDate>"
     *
     * @param args The arguments after the snooze command.
     * @return A String array of [taskIndex (0-based as string), newDate].
     * @throws BobException If the format is invalid.
     */
    public static String[] parseSnoozeDeadline(String args)
            throws BobException {
        if (!args.contains(" /to ")) {
            throw new BobException(
                    "When should I reschedule it to?~\n"
                    + "    Deadline: snooze <task#> /to <new date>\n"
                    + "    Event: snooze <task#> /from <start>"
                    + " /to <end>");
        }
        String[] parts = args.split(" /to ", 2);
        String indexStr = parts[0].trim();
        String newDate = parts[1].trim();
        if (newDate.isEmpty()) {
            throw new BobException("The new date can't be empty~");
        }
        try {
            int index = Integer.parseInt(indexStr) - 1;
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
        String[] parts = args.split(" /from ", 2);
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
        try {
            int index = Integer.parseInt(indexStr) - 1;
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
        if (!args.contains(" /slot ")) {
            throw new BobException("I need time slots for tentative events~\n"
                    + "    Try: tentative <description> /slot <from> - <to>"
                    + " /slot <from> - <to>");
        }
        String[] parts = args.split(" /slot ");
        String description = parts[0].trim();
        if (description.isEmpty()) {
            throw new BobException("What's the event called?~\n"
                    + "    Try: tentative <description> /slot ...");
        }

        List<String[]> slots = new ArrayList<>();
        for (int i = 1; i < parts.length; i++) {
            String slotStr = parts[i].trim();
            if (!slotStr.contains(" - ")) {
                throw new BobException("Each slot needs a 'from - to' format~\n"
                        + "    e.g. /slot Mon 2pm - 4pm");
            }
            String[] timeParts = slotStr.split(" - ", 2);
            slots.add(new String[]{timeParts[0].trim(), timeParts[1].trim()});
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
        if (!args.contains(" /slot ")) {
            throw new BobException("Which slot to confirm?~\n"
                    + "    Try: confirm <task#> /slot <slot#>");
        }
        String[] parts = args.split(" /slot ");
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
