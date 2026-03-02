package boba.ui;

import boba.task.Task;
import boba.task.TaskList;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.IntStream;

/**
 * Handles all user interface interactions including displaying messages and reading input.
 */
public class Ui {
    private static final String LINE = "✿═══════════════════════════════════════════════✿";
    private static final String LOGO = "\n"
            + "    ██████╗  ██████╗ ██████╗  █████╗ \n"
            + "    ██╔══██╗██╔═══██╗██╔══██╗██╔══██╗\n"
            + "    ██████╔╝██║   ██║██████╔╝███████║\n"
            + "    ██╔══██╗██║   ██║██╔══██╗██╔══██║\n"
            + "    ██████╔╝╚██████╔╝██████╔╝██║  ██║\n"
            + "    ╚═════╝  ╚═════╝ ╚═════╝ ╚═╝  ╚═╝\n"
            + "          ☆ your bubbly assistant ☆\n";

    private Scanner scanner;

    /**
     * Creates a new Ui instance and initializes the input scanner.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays the welcome message with the Boba logo.
     */
    public void showWelcome() {
        showLine();
        System.out.println(LOGO);
        System.out.println("    Hii! I'm Boba ◕‿◕");
        System.out.println("    What can I do for you today?");
        showLine();
    }

    /**
     * Displays the goodbye message.
     */
    public void showGoodbye() {
        showLine();
        System.out.println("    Bye bye :) Hope to see you again soon! ♡");
        showLine();
    }

    /**
     * Displays a decorative divider line.
     */
    public void showLine() {
        System.out.println(LINE);
    }

    /**
     * Reads a command from the user.
     *
     * @return The user's input as a string.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays an error message to the user.
     *
     * @param message The error message to display.
     */
    public void showError(String message) {
        System.out.println("    " + message);
    }

    /**
     * Displays one or more error messages to the user.
     *
     * @param messages The error messages to display.
     */
    public void showErrors(String... messages) {
        for (String message : messages) {
            showError(message);
        }
    }

    /**
     * Displays a message confirming a task was added.
     *
     * @param task The task that was added.
     * @param taskCount The total number of tasks after adding.
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("    Got it! I've added this task ✿");
        System.out.println("      " + task);
        System.out.println("    Now you have " + taskCount + " task(s) in the list~");
    }

    /**
     * Displays a message confirming a task was deleted.
     *
     * @param task The task that was deleted.
     * @param taskCount The total number of tasks after deletion.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println("    Alright, I've removed this task~");
        System.out.println("      " + task);
        System.out.println("    Now you have " + taskCount + " task(s) in the list.");
    }

    /**
     * Displays a message confirming a task was marked as done.
     *
     * @param task The task that was marked.
     */
    public void showTaskMarked(Task task) {
        System.out.println("    Yay you did it!! ☆ﾟ.*･｡ﾟ");
        System.out.println("    " + task);
    }

    /**
     * Displays a message confirming a task was unmarked.
     *
     * @param task The task that was unmarked.
     */
    public void showTaskUnmarked(Task task) {
        System.out.println("    No worries, we all need more time sometimes~");
        System.out.println("    " + task);
    }

    /**
     * Displays all tasks in the task list.
     *
     * @param tasks The TaskList to display.
     */
    public void showTaskList(TaskList tasks) {
        System.out.println("    Okie here's everything on your plate~ 🍡");
        IntStream.range(0, tasks.size())
                .forEach(i -> System.out.println(
                        "    " + (i + 1) + "." + tasks.get(i)));
    }

    /**
     * Displays an error message when loading saved tasks fails.
     */
    public void showLoadingError() {
        System.out.println("    Hmm couldn't load saved tasks~");
    }

    /**
     * Displays tasks that match a search keyword.
     *
     * @param matchingTasks The list of tasks that match the search.
     */
    public void showFoundTasks(ArrayList<Task> matchingTasks) {
        if (matchingTasks.isEmpty()) {
            System.out.println("    Hmm no tasks match that keyword~");
        } else {
            System.out.println("    Here are the matching tasks in your list~ ✿");
            IntStream.range(0, matchingTasks.size())
                    .forEach(i -> System.out.println(
                            "    " + (i + 1) + "." + matchingTasks.get(i)));
        }
    }

    /**
     * Displays a motivational quote.
     *
     * @param quote The quote to display.
     */
    public void showCheer(String quote) {
        System.out.println("    ✨ " + quote + " ✨");
    }

    /**
     * Closes the input scanner.
     */
    public void close() {
        scanner.close();
    }
}
