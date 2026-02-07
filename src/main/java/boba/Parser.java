package boba;

public class Parser {

    public static String getCommand(String input) {
        return input.split(" ")[0];
    }

    public static String getArguments(String input) {
        int spaceIndex = input.indexOf(" ");
        if (spaceIndex == -1) {
            return "";
        }
        return input.substring(spaceIndex + 1).trim();
    }

    public static int parseIndex(String input) throws NumberFormatException {
        String[] parts = input.split(" ");
        if (parts.length < 2) {
            throw new NumberFormatException("No index provided");
        }
        return Integer.parseInt(parts[1]) - 1;
    }

    public static Todo parseTodo(String args) throws BobException {
        if (args.isEmpty()) {
            throw new BobException("Uhh what's the task? Can't be empty~\n    Try: todo <description>");
        }
        return new Todo(args);
    }

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
