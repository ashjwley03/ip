package boba;

import boba.exception.BobException;
import boba.parser.Parser;
import boba.storage.Storage;
import boba.task.Deadline;
import boba.task.DoAfter;
import boba.task.DoWithin;
import boba.task.Event;
import boba.task.FixedDuration;
import boba.task.Task;
import boba.task.TaskList;
import boba.task.TentativeEvent;
import boba.task.Todo;
import boba.ui.Ui;
import boba.util.CheerLoader;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

/**
 * Main class for the Boba chatbot application.
 * Boba is a personal task manager that helps users track todos, deadlines, and events.
 */
public class Boba {
    private Storage storage;
    private TaskList tasks;
    private Ui ui;
    private CheerLoader cheerLoader;

    /**
     * Creates a new Boba chatbot instance.
     *
     * @param filePath The file path where tasks will be saved and loaded from.
     */
    public Boba(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        cheerLoader = new CheerLoader();
        try {
            tasks = new TaskList(storage.load());
        } catch (BobException e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }
    }

    /**
     * Runs the main loop of the chatbot, processing user commands until exit.
     */
    public void run() {
        ui.showWelcome();
        boolean isExit = false;

        while (!isExit) {
            String input = ui.readCommand();
            String command = Parser.getCommand(input);
            String args = Parser.getArguments(input);

            ui.showLine();

            try {
                if (command.equals("bye")) {
                    isExit = true;
                } else {
                    processCommandCli(command, args, input);
                }
            } catch (BobException e) {
                ui.showError(e.getMessage());
            } catch (NumberFormatException e) {
                ui.showErrors("That's not a number silly~",
                        "Try: " + command + " <number>");
            } catch (ArrayIndexOutOfBoundsException e) {
                ui.showErrors("Hmm something's off with that format~",
                        "Check your command and try again!");
            }

            if (!isExit) {
                ui.showLine();
            }
        }

        ui.showGoodbye();
        ui.close();
    }

    private void processCommandCli(String command, String args, String input)
            throws BobException {
        switch (command) {
        case "list":
            ui.showTaskList(tasks);
            break;
        case "mark":
            int markIndex = Parser.parseIndex(input);
            if (markIndex < 0 || markIndex >= tasks.size()) {
                ui.showErrors("Hmm that task doesn't exist!",
                        "You have " + tasks.size() + " task(s) btw~");
            } else {
                tasks.get(markIndex).markAsDone();
                ui.showTaskMarked(tasks.get(markIndex));
                Task nextTask = createNextIfRecurring(
                        tasks.get(markIndex));
                if (nextTask != null) {
                    tasks.add(nextTask);
                    ui.showError("Recurring! Next: " + nextTask);
                }
                storage.save(tasks);
            }
            break;
        case "unmark":
            int unmarkIndex = Parser.parseIndex(input);
            if (unmarkIndex < 0 || unmarkIndex >= tasks.size()) {
                ui.showErrors("Hmm that task doesn't exist!",
                        "You have " + tasks.size() + " task(s) btw~");
            } else {
                tasks.get(unmarkIndex).markAsNotDone();
                storage.save(tasks);
                ui.showTaskUnmarked(tasks.get(unmarkIndex));
            }
            break;
        case "todo":
            Todo todo = Parser.parseTodo(args);
            tasks.add(todo);
            storage.save(tasks);
            ui.showTaskAdded(todo, tasks.size());
            break;
        case "deadline":
            Deadline deadline = Parser.parseDeadline(args);
            tasks.add(deadline);
            storage.save(tasks);
            ui.showTaskAdded(deadline, tasks.size());
            break;
        case "event":
            Event event = Parser.parseEvent(args);
            tasks.add(event);
            storage.save(tasks);
            ui.showTaskAdded(event, tasks.size());
            break;
        case "delete":
            int deleteIndex = Parser.parseIndex(input);
            if (deleteIndex < 0 || deleteIndex >= tasks.size()) {
                ui.showErrors("Hmm that task doesn't exist!",
                        "You have " + tasks.size() + " task(s) btw~");
            } else {
                Task removed = tasks.delete(deleteIndex);
                storage.save(tasks);
                ui.showTaskDeleted(removed, tasks.size());
            }
            break;
        case "find":
            if (args.isEmpty()) {
                ui.showErrors("What should I search for?~",
                        "Try: find <keyword>");
            } else {
                ui.showFoundTasks(tasks.find(args));
            }
            break;
        case "doafter":
            DoAfter doAfter = Parser.parseDoAfter(args);
            tasks.add(doAfter);
            storage.save(tasks);
            ui.showTaskAdded(doAfter, tasks.size());
            break;
        case "dowithin":
            DoWithin doWithin = Parser.parseDoWithin(args);
            tasks.add(doWithin);
            storage.save(tasks);
            ui.showTaskAdded(doWithin, tasks.size());
            break;
        case "fixed":
            FixedDuration fd = Parser.parseFixedDuration(args);
            tasks.add(fd);
            storage.save(tasks);
            ui.showTaskAdded(fd, tasks.size());
            break;
        case "snooze":
            handleSnoozeRun(args);
            break;
        case "tentative":
            TentativeEvent tentEvent = Parser.parseTentative(args);
            tasks.add(tentEvent);
            storage.save(tasks);
            ui.showTaskAdded(tentEvent, tasks.size());
            break;
        case "confirm":
            handleConfirmRun(args);
            break;
        case "remind":
            ui.showError(getReminders());
            break;
        case "cheer":
            ui.showCheer(cheerLoader.getRandomQuote());
            break;
        default:
            ui.showErrors("Hmm I don't know that one~",
                    "Try: todo, deadline, event, doafter,"
                            + " dowithin, fixed, snooze,"
                            + " tentative, confirm, remind,"
                            + " list, mark, unmark, delete,"
                            + " find, cheer, or bye!");
            break;
        }
    }

    private void handleConfirmRun(String args) throws BobException {
        int[] confirmArgs = Parser.parseConfirm(args);
        int taskIdx = confirmArgs[0];
        int slotIdx = confirmArgs[1];
        if (taskIdx < 0 || taskIdx >= tasks.size()) {
            ui.showErrors("Hmm that task doesn't exist!",
                    "You have " + tasks.size() + " task(s) btw~");
        } else if (!(tasks.get(taskIdx) instanceof TentativeEvent)) {
            ui.showError("That's not a tentative event~");
        } else {
            TentativeEvent te = (TentativeEvent) tasks.get(taskIdx);
            if (slotIdx < 0 || slotIdx >= te.getSlotCount()) {
                ui.showErrors("Invalid slot number!",
                        "This event has " + te.getSlotCount()
                                + " slot(s).");
            } else {
                Event confirmed = te.confirm(slotIdx);
                tasks.delete(taskIdx);
                tasks.add(confirmed);
                storage.save(tasks);
                ui.showError("Confirmed! Your event is now set:");
                ui.showError("  " + confirmed);
            }
        }
    }

    /**
     * Generates a response for the user's chat message.
     *
     * @param input The user's input string.
     * @return The chatbot's response string.
     */
    public String getResponse(String input) {
        String command = Parser.getCommand(input);
        String args = Parser.getArguments(input);
        StringBuilder response = new StringBuilder();

        try {
            switch (command) {
            case "bye":
                response.append("Bye bye :) Hope to see you again soon! \u2661");
                break;

            case "list":
                response.append(formatTaskList());
                break;

            case "mark":
                int markIndex = Parser.parseIndex(input);
                if (!isValidIndex(markIndex)) {
                    response.append(formatInvalidIndexError());
                } else {
                    tasks.get(markIndex).markAsDone();
                    response.append(
                            "Yay you did it!! \u2606\uff9f.*\uff65\uff61\uff9f\n");
                    response.append(tasks.get(markIndex));
                    Task nextTask = createNextIfRecurring(
                            tasks.get(markIndex));
                    if (nextTask != null) {
                        tasks.add(nextTask);
                        response.append(
                                "\n\n\uD83D\uDD01 Recurring! Next:\n  "
                                + nextTask);
                    }
                    storage.save(tasks);
                }
                break;

            case "unmark":
                int unmarkIndex = Parser.parseIndex(input);
                if (!isValidIndex(unmarkIndex)) {
                    response.append(formatInvalidIndexError());
                } else {
                    tasks.get(unmarkIndex).markAsNotDone();
                    storage.save(tasks);
                    response.append("No worries, we all need more time"
                            + " sometimes~\n");
                    response.append(tasks.get(unmarkIndex));
                }
                break;

            case "todo":
                response.append(addTaskAndRespond(Parser.parseTodo(args)));
                break;

            case "deadline":
                response.append(addTaskAndRespond(Parser.parseDeadline(args)));
                break;

            case "event":
                response.append(addTaskAndRespond(Parser.parseEvent(args)));
                break;

            case "delete":
                int deleteIndex = Parser.parseIndex(input);
                if (!isValidIndex(deleteIndex)) {
                    response.append(formatInvalidIndexError());
                } else {
                    Task removed = tasks.delete(deleteIndex);
                    storage.save(tasks);
                    response.append("Alright, I've removed this task~\n");
                    response.append("  " + removed + "\n");
                    response.append("Now you have " + tasks.size()
                            + " task(s) in the list.");
                }
                break;

            case "find":
                response.append(findAndRespond(args));
                break;

            case "doafter":
                response.append(
                        addTaskAndRespond(Parser.parseDoAfter(args)));
                break;

            case "dowithin":
                response.append(
                        addTaskAndRespond(Parser.parseDoWithin(args)));
                break;

            case "fixed":
                response.append(
                        addTaskAndRespond(
                                Parser.parseFixedDuration(args)));
                break;

            case "snooze":
                response.append(snoozeAndRespond(args));
                break;

            case "tentative":
                response.append(addTaskAndRespond(Parser.parseTentative(args)));
                break;

            case "confirm":
                response.append(confirmSlotAndRespond(args));
                break;

            case "remind":
                response.append(getReminders());
                break;

            case "cheer":
                response.append("\u2728 " + cheerLoader.getRandomQuote()
                        + " \u2728");
                break;

            default:
                response.append("Hmm I don't know that one~\n");
                response.append("Try: todo, deadline, event, doafter,"
                        + " dowithin, fixed, snooze, tentative,"
                        + " confirm, remind, list, mark, unmark,"
                        + " delete, find, cheer, or bye!");
                break;
            }
        } catch (BobException e) {
            response.append(e.getMessage());
        } catch (NumberFormatException e) {
            response.append("That's not a number silly~\n");
            response.append("Try: " + command + " <number>");
        } catch (ArrayIndexOutOfBoundsException e) {
            response.append("Hmm something's off with that format~\n");
            response.append("Check your command and try again!");
        }

        return response.toString();
    }

    private Task createNextIfRecurring(Task task) {
        if (!task.isRecurring()) {
            return null;
        }
        String freq = task.getRecurrence();
        if (task instanceof Deadline) {
            return ((Deadline) task).createNextOccurrence(freq);
        } else if (task instanceof Event) {
            return ((Event) task).createNextOccurrence(freq);
        } else if (task instanceof Todo) {
            return ((Todo) task).createNextOccurrence(freq);
        }
        return null;
    }

    /**
     * Returns a reminders string listing overdue and upcoming deadlines.
     * Checks for overdue tasks and tasks due within the next 7 days.
     *
     * @return A formatted reminder string.
     */
    public String getReminders() {
        LocalDate today = LocalDate.now();
        LocalDate weekAhead = today.plus(7, ChronoUnit.DAYS);

        ArrayList<String> overdue = new ArrayList<>();
        ArrayList<String> upcoming = new ArrayList<>();

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.isDone()) {
                continue;
            }
            if (!(task instanceof Deadline)) {
                continue;
            }
            Deadline d = (Deadline) task;
            if (!d.hasDate()) {
                continue;
            }
            LocalDate dueDate = d.getByDate();
            int num = i + 1;
            if (dueDate.isBefore(today)) {
                overdue.add("  " + num + "." + d);
            } else if (!dueDate.isAfter(weekAhead)) {
                upcoming.add("  " + num + "." + d);
            }
        }

        if (overdue.isEmpty() && upcoming.isEmpty()) {
            return "\u2705 No urgent reminders! You're all caught up~";
        }

        StringBuilder sb = new StringBuilder();
        if (!overdue.isEmpty()) {
            sb.append("\u26A0\uFE0F OVERDUE:\n");
            sb.append(String.join("\n", overdue));
        }
        if (!upcoming.isEmpty()) {
            if (!overdue.isEmpty()) {
                sb.append("\n\n");
            }
            sb.append("\u23F0 Due within 7 days:\n");
            sb.append(String.join("\n", upcoming));
        }
        return sb.toString();
    }

    private boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }

    private String formatInvalidIndexError() {
        return "Hmm that task doesn't exist!\n"
                + "You have " + tasks.size() + " task(s) btw~";
    }

    private String addTaskAndRespond(Task task) {
        tasks.add(task);
        storage.save(tasks);
        return "Got it! I've added this task \u273F\n"
                + "  " + task + "\n"
                + "Now you have " + tasks.size() + " task(s) in the list~";
    }

    private String formatTaskList() {
        StringBuilder sb = new StringBuilder(
                "Okie here's everything on your plate~ \uD83C\uDF61\n");
        for (int i = 0; i < tasks.size(); i++) {
            sb.append((i + 1) + "." + tasks.get(i));
            if (i < tasks.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private void handleSnoozeRun(String args) throws BobException {
        boolean isEventSnooze = args.contains(" /from ");
        if (isEventSnooze) {
            String[] parsed = Parser.parseSnoozeEvent(args);
            int idx = Integer.parseInt(parsed[0]);
            if (idx < 0 || idx >= tasks.size()) {
                ui.showErrors("Hmm that task doesn't exist!",
                        "You have " + tasks.size() + " task(s) btw~");
            } else if (!(tasks.get(idx) instanceof Event)) {
                ui.showError("That's not an event task~");
            } else {
                Event e = (Event) tasks.get(idx);
                e.reschedule(parsed[1], parsed[2]);
                storage.save(tasks);
                ui.showError("Snoozed! Here's the updated task:");
                ui.showError("  " + e);
            }
        } else {
            String[] parsed = Parser.parseSnoozeDeadline(args);
            int idx = Integer.parseInt(parsed[0]);
            if (idx < 0 || idx >= tasks.size()) {
                ui.showErrors("Hmm that task doesn't exist!",
                        "You have " + tasks.size() + " task(s) btw~");
            } else if (!(tasks.get(idx) instanceof Deadline)) {
                ui.showError("That's not a deadline task~\n"
                        + "For events: snooze <task#> /from <start>"
                        + " /to <end>");
            } else {
                Deadline d = (Deadline) tasks.get(idx);
                d.reschedule(parsed[1]);
                storage.save(tasks);
                ui.showError("Snoozed! Here's the updated task:");
                ui.showError("  " + d);
            }
        }
    }

    private String snoozeAndRespond(String args) throws BobException {
        boolean isEventSnooze = args.contains(" /from ");

        if (isEventSnooze) {
            String[] parsed = Parser.parseSnoozeEvent(args);
            int idx = Integer.parseInt(parsed[0]);
            if (!isValidIndex(idx)) {
                return formatInvalidIndexError();
            }
            if (!(tasks.get(idx) instanceof Event)) {
                return "That's not an event task~";
            }
            Event e = (Event) tasks.get(idx);
            e.reschedule(parsed[1], parsed[2]);
            storage.save(tasks);
            return "Snoozed! Here's the updated task \u23F0\n  " + e;
        } else {
            String[] parsed = Parser.parseSnoozeDeadline(args);
            int idx = Integer.parseInt(parsed[0]);
            if (!isValidIndex(idx)) {
                return formatInvalidIndexError();
            }
            if (!(tasks.get(idx) instanceof Deadline)) {
                return "That's not a deadline task~\n"
                        + "For events: snooze <task#>"
                        + " /from <start> /to <end>";
            }
            Deadline d = (Deadline) tasks.get(idx);
            d.reschedule(parsed[1]);
            storage.save(tasks);
            return "Snoozed! Here's the updated task \u23F0\n  " + d;
        }
    }

    private String confirmSlotAndRespond(String args) throws BobException {
        int[] confirmArgs = Parser.parseConfirm(args);
        int taskIdx = confirmArgs[0];
        int slotIdx = confirmArgs[1];

        if (!isValidIndex(taskIdx)) {
            return formatInvalidIndexError();
        }
        if (!(tasks.get(taskIdx) instanceof TentativeEvent)) {
            return "That's not a tentative event~";
        }
        TentativeEvent te = (TentativeEvent) tasks.get(taskIdx);
        if (slotIdx < 0 || slotIdx >= te.getSlotCount()) {
            return "Invalid slot number!\nThis event has "
                    + te.getSlotCount() + " slot(s).";
        }
        Event confirmed = te.confirm(slotIdx);
        tasks.delete(taskIdx);
        tasks.add(confirmed);
        storage.save(tasks);
        return "Confirmed! Your event is now set \u2728\n  " + confirmed;
    }

    private String findAndRespond(String keyword) {
        if (keyword.isEmpty()) {
            return "What should I search for?~\nTry: find <keyword>";
        }
        ArrayList<Task> matches = tasks.find(keyword);
        if (matches.isEmpty()) {
            return "Hmm no tasks match that keyword~";
        }
        StringBuilder sb = new StringBuilder(
                "Here are the matching tasks in your list~ \u273F\n");
        for (int i = 0; i < matches.size(); i++) {
            sb.append((i + 1) + "." + matches.get(i));
            if (i < matches.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Entry point for the Boba application.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        new Boba("./data/boba.txt").run();
    }
}
