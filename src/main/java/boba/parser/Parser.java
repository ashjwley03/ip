package boba.parser;

import boba.exception.BobException;
import boba.task.Deadline;
import boba.task.Event;
import boba.task.TentativeEvent;
import boba.task.Todo;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides utility methods for parsing user input commands.
 */
public class Parser {

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
            throw new BobException("Uhh what's the task? Can't be empty~\n    Try: todo <description>");
        }
        return new Todo(args);
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
        if (!args.contains(" /by ")) {
            throw new BobException("When's it due? Add /by <date>~\n"
                    + "    Try: deadline <description> /by <when>");
        }
        String[] parts = args.split(" /by ");
        String description = parts[0].trim();
        String by = parts[1].trim();
        if (description.isEmpty() || by.isEmpty()) {
            throw new BobException("Hmm something's missing there~\n"
                    + "    Try: deadline <description> /by <when>");
        }
        return new Deadline(description, by);
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
        if (!args.contains(" /from ") || !args.contains(" /to ")) {
            throw new BobException("I need both /from and /to times~\n"
                    + "    Try: event <description> /from <start> /to <end>");
        }
        String[] parts = args.split(" /from ");
        String description = parts[0].trim();
        String[] timeParts = parts[1].split(" /to ");
        String from = timeParts[0].trim();
        String to = timeParts[1].trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new BobException("Hmm something's missing there~\n"
                    + "    Try: event <description> /from <start> /to <end>");
        }
        return new Event(description, from, to);
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
