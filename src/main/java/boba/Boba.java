package boba;

import boba.exception.BobException;
import boba.parser.Parser;
import boba.storage.Storage;
import boba.task.Deadline;
import boba.task.Event;
import boba.task.Task;
import boba.task.TaskList;
import boba.task.Todo;
import boba.ui.Ui;
import boba.util.CheerLoader;

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
                switch (command) {
                case "bye":
                    isExit = true;
                    break;

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
                        storage.save(tasks);
                        ui.showTaskMarked(tasks.get(markIndex));
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

                case "cheer":
                    ui.showCheer(cheerLoader.getRandomQuote());
                    break;

                default:
                    ui.showErrors("Hmm I don't know that one~",
                            "Try: todo, deadline, event, list, mark, unmark, delete, find, cheer, or bye!");
                    break;
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
                response.append("Bye bye :) Hope to see you again soon! ♡");
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
                    storage.save(tasks);
                    response.append("Yay you did it!! ☆ﾟ.*･｡ﾟ\n");
                    response.append(tasks.get(markIndex));
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

            case "cheer":
                response.append("\u2728 " + cheerLoader.getRandomQuote()
                        + " \u2728");
                break;

            default:
                response.append("Hmm I don't know that one~\n");
                response.append("Try: todo, deadline, event, list,"
                        + " mark, unmark, delete, find, cheer, or bye!");
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
        StringBuilder sb = new StringBuilder("Okie here's everything on your plate~ \uD83C\uDF61\n");
        for (int i = 0; i < tasks.size(); i++) {
            sb.append((i + 1) + "." + tasks.get(i));
            if (i < tasks.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
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
