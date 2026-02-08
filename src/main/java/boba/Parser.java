package boba;

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
            throw new BobException("When's it due? Add /by <date>~\n    Try: deadline <description> /by <when>");
        }
        String[] parts = args.split(" /by ");
        String description = parts[0].trim();
        String by = parts[1].trim();
        if (description.isEmpty() || by.isEmpty()) {
            throw new BobException("Hmm something's missing there~\n    Try: deadline <description> /by <when>");
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
            throw new BobException("I need both /from and /to times~\n    Try: event <description> /from <start> /to <end>");
        }
        String[] parts = args.split(" /from ");
        String description = parts[0].trim();
        String[] timeParts = parts[1].split(" /to ");
        String from = timeParts[0].trim();
        String to = timeParts[1].trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new BobException("Hmm something's missing there~\n    Try: event <description> /from <start> /to <end>");
        }
        return new Event(description, from, to);
    }
}
