package boba;

import java.util.Scanner;

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

    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    public void showWelcome() {
        showLine();
        System.out.println(LOGO);
        System.out.println("    Hii! I'm Boba ◕‿◕");
        System.out.println("    What can I do for you today?");
        showLine();
    }

    public void showGoodbye() {
        showLine();
        System.out.println("    Bye bye :) Hope to see you again soon! ♡");
        showLine();
    }

    public void showLine() {
        System.out.println(LINE);
    }

    public String readCommand() {
        return scanner.nextLine();
    }

    public void showError(String message) {
        System.out.println("    " + message);
    }

    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("    Got it! I've added this task ✿");
        System.out.println("      " + task);
        System.out.println("    Now you have " + taskCount + " task(s) in the list~");
    }

    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println("    Alright, I've removed this task~");
        System.out.println("      " + task);
        System.out.println("    Now you have " + taskCount + " task(s) in the list.");
    }

    public void showTaskMarked(Task task) {
        System.out.println("    Yay you did it!! ☆ﾟ.*･｡ﾟ");
        System.out.println("    " + task);
    }

    public void showTaskUnmarked(Task task) {
        System.out.println("    No worries, we all need more time sometimes~");
        System.out.println("    " + task);
    }

    public void showTaskList(TaskList tasks) {
        System.out.println("    Okie here's everything on your plate~ 🍡");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println("    " + (i + 1) + "." + tasks.get(i));
        }
    }

    public void showLoadingError() {
        System.out.println("    Hmm couldn't load saved tasks~");
    }

    public void close() {
        scanner.close();
    }
}
