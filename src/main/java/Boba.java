public class Boba {
    private Storage storage;
    private TaskList tasks;
    private Ui ui;

    public Boba(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
        } catch (BobException e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }
    }

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
                            ui.showError("Hmm that task doesn't exist!");
                            ui.showError("You have " + tasks.size() + " task(s) btw~");
                        } else {
                            tasks.get(markIndex).markAsDone();
                            storage.save(tasks);
                            ui.showTaskMarked(tasks.get(markIndex));
                        }
                        break;

                    case "unmark":
                        int unmarkIndex = Parser.parseIndex(input);
                        if (unmarkIndex < 0 || unmarkIndex >= tasks.size()) {
                            ui.showError("Hmm that task doesn't exist!");
                            ui.showError("You have " + tasks.size() + " task(s) btw~");
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
                            ui.showError("Hmm that task doesn't exist!");
                            ui.showError("You have " + tasks.size() + " task(s) btw~");
                        } else {
                            Task removed = tasks.delete(deleteIndex);
                            storage.save(tasks);
                            ui.showTaskDeleted(removed, tasks.size());
                        }
                        break;

                    default:
                        ui.showError("Hmm I don't know that one~");
                        ui.showError("Try: todo, deadline, event, list, mark, unmark, delete, or bye!");
                        break;
                }
            } catch (BobException e) {
                ui.showError(e.getMessage());
            } catch (NumberFormatException e) {
                ui.showError("That's not a number silly~");
                ui.showError("Try: " + command + " <number>");
            } catch (ArrayIndexOutOfBoundsException e) {
                ui.showError("Hmm something's off with that format~");
                ui.showError("Check your command and try again!");
            }

            if (!isExit) {
                ui.showLine();
            }
        }

        ui.showGoodbye();
        ui.close();
    }

    public static void main(String[] args) {
        new Boba("./data/boba.txt").run();
    }
}
