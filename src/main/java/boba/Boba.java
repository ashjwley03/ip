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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;

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
            if (input.trim().isEmpty()) {
                continue;
            }
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
                ui.showErrors("That's not a number, silly~",
                        "Try: " + command + " <number>");
            } catch (ArrayIndexOutOfBoundsException e) {
                ui.showErrors("Hmm that recipe doesn't look right~",
                        "Check your command and try again!");
            }

            if (!isExit) {
                ui.showLine();
            }
        }

        ui.showGoodbye();
        ui.close();
    }

    private void showAnomalyWarningsCli(Task task) {
        String warnings = detectAnomalies(task);
        if (!warnings.isEmpty()) {
            ui.showError(warnings);
        }
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
                ui.showErrors("Hmm can't find that pearl!",
                        "You have " + tasks.size() + " task(s) btw~");
            } else {
                tasks.get(markIndex).markAsDone();
                ui.showTaskMarked(tasks.get(markIndex));
                Task nextTask = createNextIfRecurring(
                        tasks.get(markIndex));
                if (nextTask != null) {
                    tasks.add(nextTask);
                    ui.showError("Refill! Next: " + nextTask);
                }
                storage.save(tasks);
            }
            break;
        case "unmark":
            int unmarkIndex = Parser.parseIndex(input);
            if (unmarkIndex < 0 || unmarkIndex >= tasks.size()) {
                ui.showErrors("Hmm can't find that pearl!",
                        "You have " + tasks.size() + " task(s) btw~");
            } else {
                tasks.get(unmarkIndex).markAsNotDone();
                storage.save(tasks);
                ui.showTaskUnmarked(tasks.get(unmarkIndex));
            }
            break;
        case "todo":
            Todo todo = Parser.parseTodo(args);
            showAnomalyWarningsCli(todo);
            tasks.add(todo);
            storage.save(tasks);
            ui.showTaskAdded(todo, tasks.size());
            break;
        case "deadline":
            Deadline deadline = Parser.parseDeadline(args);
            showAnomalyWarningsCli(deadline);
            tasks.add(deadline);
            storage.save(tasks);
            ui.showTaskAdded(deadline, tasks.size());
            break;
        case "event":
            Event event = Parser.parseEvent(args);
            showAnomalyWarningsCli(event);
            tasks.add(event);
            storage.save(tasks);
            ui.showTaskAdded(event, tasks.size());
            break;
        case "delete":
            int deleteIndex = Parser.parseIndex(input);
            if (deleteIndex < 0 || deleteIndex >= tasks.size()) {
                ui.showErrors("Hmm can't find that pearl!",
                        "You have " + tasks.size() + " task(s) btw~");
            } else {
                Task removed = tasks.delete(deleteIndex);
                storage.save(tasks);
                ui.showTaskDeleted(removed, tasks.size());
            }
            break;
        case "find":
            if (args.isEmpty()) {
                ui.showErrors("What flavor are we looking for?~",
                        "Try: find <keyword>");
            } else {
                ui.showFoundTasks(tasks.find(args));
            }
            break;
        case "doafter":
            DoAfter doAfter = Parser.parseDoAfter(args);
            showAnomalyWarningsCli(doAfter);
            tasks.add(doAfter);
            storage.save(tasks);
            ui.showTaskAdded(doAfter, tasks.size());
            break;
        case "dowithin":
            DoWithin doWithin = Parser.parseDoWithin(args);
            showAnomalyWarningsCli(doWithin);
            tasks.add(doWithin);
            storage.save(tasks);
            ui.showTaskAdded(doWithin, tasks.size());
            break;
        case "fixed":
            FixedDuration fd = Parser.parseFixedDuration(args);
            showAnomalyWarningsCli(fd);
            tasks.add(fd);
            storage.save(tasks);
            ui.showTaskAdded(fd, tasks.size());
            break;
        case "snooze":
            handleSnoozeRun(args);
            break;
        case "tentative":
            TentativeEvent tentEvent = Parser.parseTentative(args);
            showAnomalyWarningsCli(tentEvent);
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
        case "freetime":
            ui.showError(findFreeTimes(args));
            break;
        case "schedule":
            ui.showError(viewSchedule(args));
            break;
        case "cheer":
            ui.showCheer(cheerLoader.getRandomQuote());
            break;
        case "boba":
            ui.showError(recommendBoba());
            break;
        default:
            ui.showErrors("Hmm that's not on the menu~",
                    "Try: todo, deadline, event, doafter,"
                            + " dowithin, fixed, snooze,"
                            + " tentative, confirm, remind,"
                            + " freetime, schedule, list, mark,"
                            + " unmark, delete, find, boba,"
                            + " cheer, or bye!");
            break;
        }
    }

    private void handleConfirmRun(String args) throws BobException {
        int[] confirmArgs = Parser.parseConfirm(args);
        int taskIdx = confirmArgs[0];
        int slotIdx = confirmArgs[1];
        if (taskIdx < 0 || taskIdx >= tasks.size()) {
            ui.showErrors("Hmm can't find that pearl!",
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
                ui.showError("Sealed the lid! Event is set:");
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
        if (input == null || input.trim().isEmpty()) {
            return "Type something~ I can't read "
                    + "blank bubbles!";
        }
        String command = Parser.getCommand(input);
        String args = Parser.getArguments(input);
        StringBuilder response = new StringBuilder();

        try {
            switch (command) {
            case "bye":
                response.append("Bye bye! Stay sweet like brown sugar boba~ \u2661");
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
                            "Nice, one less pearl to chew on! \uD83E\uDD64\n");
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
                    response.append("No rush~ let it steep a "
                            + "little longer!\n");
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
                    response.append("Tossed it out like old tea "
                            + "leaves~\n");
                    response.append("  " + removed + "\n");
                    response.append("Now you have " + tasks.size()
                            + " task(s) in the cup.");
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

            case "freetime":
                response.append(findFreeTimes(args));
                break;

            case "schedule":
                response.append(viewSchedule(args));
                break;

            case "cheer":
                response.append("\uD83E\uDD64 "
                        + cheerLoader.getRandomQuote()
                        + " \uD83E\uDD64");
                break;
            case "boba":
                response.append(recommendBoba());
                break;
            default:
                response.append(getUnknownCommandMessage());
                break;
            }
        } catch (BobException e) {
            response.append(e.getMessage());
        } catch (NumberFormatException e) {
            response.append("That's not a number, silly~\n");
            response.append("Try: " + command + " <number>");
        } catch (ArrayIndexOutOfBoundsException e) {
            response.append("Hmm that recipe doesn't look right~\n");
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
            return "\u2705 No urgent reminders! Sip and relax~";
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

    private String findFreeTimes(String args) {
        int lookAheadDays = 7;
        int requiredHours = 0;
        boolean filterByHours = !args.isEmpty();

        if (filterByHours) {
            try {
                requiredHours = Integer.parseInt(args.trim());
                lookAheadDays = 14;
            } catch (NumberFormatException e) {
                return "That's not a valid number of hours~\n"
                        + "    Try: freetime <hours> or just: freetime";
            }
            if (requiredHours < 1 || requiredHours > 14) {
                return "Hours must be between 1 and 14~";
            }
        }

        LocalDate today = LocalDate.now();
        DateTimeFormatter dayFmt =
                DateTimeFormatter.ofPattern("EEE, MMM d");
        StringBuilder sb = new StringBuilder();

        if (filterByHours) {
            sb.append("\uD83D\uDD0D Looking for a day with "
                    + requiredHours + "+ free hours...\n");
        } else {
            sb.append("\uD83D\uDCC5 Your schedule for the next 7"
                    + " days:\n");
        }

        boolean found = false;
        for (int d = 0; d < lookAheadDays; d++) {
            LocalDate date = today.plus(d, ChronoUnit.DAYS);
            String dateStr = date.toString();
            ArrayList<String> dayTasks = getTasksForDate(date, dateStr);
            int busyHours = dayTasks.size();
            int freeHours = Math.max(0, 14 - busyHours);

            if (filterByHours) {
                if (freeHours >= requiredHours) {
                    sb.append("\n\u2705 " + date.format(dayFmt));
                    sb.append(" (~" + freeHours + "h free)");
                    if (!dayTasks.isEmpty()) {
                        sb.append("\n  Busy with:");
                        for (String t : dayTasks) {
                            sb.append("\n    " + t);
                        }
                    }
                    found = true;
                    break;
                }
            } else {
                sb.append("\n" + date.format(dayFmt) + ":");
                if (dayTasks.isEmpty()) {
                    sb.append(" \u2705 Free!");
                } else {
                    for (String t : dayTasks) {
                        sb.append("\n  " + t);
                    }
                    sb.append("\n  (~" + freeHours + "h free)");
                }
            }
        }

        if (filterByHours && !found) {
            sb.append("\nNo day with " + requiredHours
                    + "+ free hours found in the next "
                    + lookAheadDays + " days~");
        }

        return sb.toString();
    }

    private ArrayList<String> getTasksForDate(
            LocalDate date, String dateStr) {
        ArrayList<String> dayTasks = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.isDone()) {
                continue;
            }
            if (task instanceof Event) {
                Event ev = (Event) task;
                if (ev.getFrom().contains(dateStr)) {
                    dayTasks.add(task.toString());
                }
            } else if (task instanceof Deadline) {
                Deadline dl = (Deadline) task;
                if (dl.hasDate() && dl.getByDate().equals(date)) {
                    dayTasks.add(task.toString());
                }
            }
        }
        return dayTasks;
    }

    private String viewSchedule(String args) {
        LocalDate date;
        if (args.isEmpty()) {
            date = LocalDate.now();
        } else {
            try {
                date = LocalDate.parse(args.trim());
            } catch (Exception e) {
                return "Invalid date format~\n"
                        + "    Try: schedule yyyy-mm-dd\n"
                        + "    Or just: schedule (for today)";
            }
        }

        String dateStr = date.toString();
        DateTimeFormatter displayFmt =
                DateTimeFormatter.ofPattern("EEEE, MMM d yyyy");
        StringBuilder sb = new StringBuilder();
        sb.append("\uD83D\uDCC6 Schedule for "
                + date.format(displayFmt) + "\n");

        ArrayList<String> events = new ArrayList<>();
        ArrayList<String> deadlines = new ArrayList<>();
        ArrayList<String> others = new ArrayList<>();

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            int num = i + 1;
            String entry = num + "." + task;

            if (task instanceof Event) {
                Event ev = (Event) task;
                if (ev.getFrom().contains(dateStr)) {
                    events.add(entry);
                }
            } else if (task instanceof Deadline) {
                Deadline dl = (Deadline) task;
                if (dl.hasDate() && dl.getByDate().equals(date)) {
                    deadlines.add(entry);
                }
            } else if (task instanceof DoWithin) {
                DoWithin dw = (DoWithin) task;
                if (dw.getStart().contains(dateStr)
                        || dw.getEnd().contains(dateStr)) {
                    others.add(entry);
                }
            }
        }

        boolean empty = events.isEmpty() && deadlines.isEmpty()
                && others.isEmpty();
        if (empty) {
            sb.append("\n\u2705 Nothing scheduled! Enjoy your"
                    + " free time~");
            return sb.toString();
        }

        if (!events.isEmpty()) {
            sb.append("\n\uD83D\uDDD3\uFE0F Events:");
            for (String e : events) {
                sb.append("\n  " + e);
            }
        }
        if (!deadlines.isEmpty()) {
            sb.append("\n\u23F0 Deadlines:");
            for (String d : deadlines) {
                sb.append("\n  " + d);
            }
        }
        if (!others.isEmpty()) {
            sb.append("\n\uD83D\uDCCB Other:");
            for (String o : others) {
                sb.append("\n  " + o);
            }
        }

        int total = events.size() + deadlines.size() + others.size();
        sb.append("\n\n" + total + " task(s) scheduled for this day.");
        return sb.toString();
    }

    private boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }

    private String formatInvalidIndexError() {
        return "Hmm can't find that pearl in the cup!\n"
                + "You have " + tasks.size() + " task(s) btw~";
    }

    private String getUnknownCommandMessage() {
        return "Hmm that's not on the menu~\n"
                + "Try: todo, deadline, event, doafter,"
                + " dowithin, fixed, snooze, tentative,"
                + " confirm, remind, freetime, schedule,"
                + " list, mark, unmark, delete, find,"
                + " boba, cheer, or bye!";
    }

    private String addTaskAndRespond(Task task) {
        String warnings = detectAnomalies(task);
        tasks.add(task);
        storage.save(tasks);
        String base = "Dropped a new pearl in! \uD83E\uDD64\n"
                + "  " + task + "\n"
                + "Now you have " + tasks.size()
                + " task(s) in the cup~";
        if (!warnings.isEmpty()) {
            return base + "\n\n" + warnings;
        }
        return base;
    }

    private String detectAnomalies(Task newTask) {
        ArrayList<String> warnings = new ArrayList<>();

        checkDuplicateDescription(newTask, warnings);

        if (newTask instanceof Event) {
            checkEventClash((Event) newTask, warnings);
        }
        if (newTask instanceof Deadline) {
            checkPastDeadline((Deadline) newTask, warnings);
        }

        if (warnings.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(
                "\u26A0\uFE0F Heads up:");
        for (String w : warnings) {
            sb.append("\n  \u2022 " + w);
        }
        return sb.toString();
    }

    private void checkDuplicateDescription(
            Task newTask, ArrayList<String> warnings) {
        String desc = newTask.getDescription().toLowerCase();
        for (int i = 0; i < tasks.size(); i++) {
            Task existing = tasks.get(i);
            if (!existing.isDone()
                    && existing.getDescription().toLowerCase()
                            .equals(desc)) {
                warnings.add("Duplicate? Task " + (i + 1)
                        + " has the same description.");
                break;
            }
        }
    }

    private void checkEventClash(
            Event newEvent, ArrayList<String> warnings) {
        String newFrom = newEvent.getFrom();
        for (int i = 0; i < tasks.size(); i++) {
            Task existing = tasks.get(i);
            if (existing.isDone() || !(existing instanceof Event)) {
                continue;
            }
            Event ex = (Event) existing;
            if (hasTimeOverlap(newFrom, newEvent.getTo(),
                    ex.getFrom(), ex.getTo())) {
                warnings.add("Clash with task " + (i + 1)
                        + ": " + ex.getDescription()
                        + " (from: " + ex.getFrom()
                        + " to: " + ex.getTo() + ")");
            }
        }
    }

    private boolean hasTimeOverlap(String from1, String to1,
            String from2, String to2) {
        if (from1.length() >= 10 && from2.length() >= 10) {
            String date1 = from1.substring(0, 10);
            String date2 = from2.substring(0, 10);
            if (!date1.equals(date2)) {
                return false;
            }
        }
        if (from1.equals(from2)) {
            return true;
        }
        if (from1.length() > 10 && from2.length() > 10
                && to1.length() > 0 && to2.length() > 0) {
            return from1.compareTo(to2) < 0
                    && from2.compareTo(to1) < 0;
        }
        return false;
    }

    private void checkPastDeadline(
            Deadline newDl, ArrayList<String> warnings) {
        if (newDl.hasDate()
                && newDl.getByDate().isBefore(LocalDate.now())) {
            warnings.add("This deadline is already past ("
                    + newDl.getByDate() + ")!");
        }
    }

    private String formatTaskList() {
        StringBuilder sb = new StringBuilder(
                "Here's what's brewing in your cup~ \uD83E\uDD64\n");
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
                ui.showErrors("Hmm can't find that pearl!",
                        "You have " + tasks.size() + " task(s) btw~");
            } else if (!(tasks.get(idx) instanceof Event)) {
                ui.showError("That's not an event task~");
            } else {
                Event e = (Event) tasks.get(idx);
                e.reschedule(parsed[1], parsed[2]);
                storage.save(tasks);
                ui.showError("Pushed it back~ new brew time:");
                ui.showError("  " + e);
            }
        } else {
            String[] parsed = Parser.parseSnoozeDeadline(args);
            int idx = Integer.parseInt(parsed[0]);
            if (idx < 0 || idx >= tasks.size()) {
                ui.showErrors("Hmm can't find that pearl!",
                        "You have " + tasks.size() + " task(s) btw~");
            } else if (!(tasks.get(idx) instanceof Deadline)) {
                ui.showError("That's not a deadline task~\n"
                        + "For events: snooze <task#> /from <start>"
                        + " /to <end>");
            } else {
                Deadline d = (Deadline) tasks.get(idx);
                d.reschedule(parsed[1]);
                storage.save(tasks);
                ui.showError("Pushed it back~ new brew time:");
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
            return "Pushed it back~ new brew time \uD83E\uDD64\n  "
                    + e;
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
            return "Pushed it back~ new brew time \uD83E\uDD64\n  "
                    + d;
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
        String warnings = detectAnomalies(confirmed);
        tasks.add(confirmed);
        storage.save(tasks);
        String base = "Sealed the lid! Your event is set \uD83E\uDD64\n  "
                + confirmed;
        if (!warnings.isEmpty()) {
            return base + "\n\n" + warnings;
        }
        return base;
    }

    private String recommendBoba() {
        String[][] drinks = {
            {"Brown Sugar Boba Milk", "Rich, caramelly, and chewy "
                    + "-- the classic that started it all!"},
            {"Taro Milk Tea", "Creamy purple goodness~ "
                    + "sweet and nutty like a hug in a cup."},
            {"Matcha Latte with Boba", "Earthy meets chewy! "
                    + "For when you need zen AND energy."},
            {"Mango Pomelo Sago", "Tropical, fruity, refreshing "
                    + "-- perfect for a sunny day!"},
            {"Oolong Milk Tea", "Smooth and fragrant~ "
                    + "a sophisticated sip for the refined palate."},
            {"Strawberry Jasmine Tea", "Floral and berry-sweet~ "
                    + "like spring in a cup!"},
            {"Thai Milk Tea", "Bold and spiced with that "
                    + "gorgeous sunset-orange color!"},
            {"Wintermelon Lemon", "Cool and citrusy~ "
                    + "when life gives you lemons, add boba!"},
            {"Honeydew Slush", "Icy melon bliss~ "
                    + "brain freeze never tasted so good."},
            {"Rose Lychee Tea", "Floral, fruity, fancy~ "
                    + "treat yourself, you deserve it!"},
        };
        Random rand = new Random();
        int idx = rand.nextInt(drinks.length);
        return "\uD83E\uDD64 Today I recommend...\n\n"
                + "  \u2B50 " + drinks[idx][0] + "\n"
                + "  " + drinks[idx][1] + "\n\n"
                + "Wanna hear another? Just type boba again!";
    }

    private String findAndRespond(String keyword) {
        if (keyword.isEmpty()) {
            return "What flavor are we looking for?~\n"
                    + "Try: find <keyword>";
        }
        ArrayList<Task> matches = tasks.find(keyword);
        if (matches.isEmpty()) {
            return "Hmm that flavor's not in the cup~";
        }
        StringBuilder sb = new StringBuilder(
                "Found these matching pearls! \uD83E\uDD64\n");
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
