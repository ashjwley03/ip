import java.util.Scanner;

public class Boba {
    public static void main(String[] args) {
        String logo = "\n"
                + "    ██████╗  ██████╗ ██████╗  █████╗ \n"
                + "    ██╔══██╗██╔═══██╗██╔══██╗██╔══██╗\n"
                + "    ██████╔╝██║   ██║██████╔╝███████║\n"
                + "    ██╔══██╗██║   ██║██╔══██╗██╔══██║\n"
                + "    ██████╔╝╚██████╔╝██████╔╝██║  ██║\n"
                + "    ╚═════╝  ╚═════╝ ╚═════╝ ╚═╝  ╚═╝\n"
                + "          ☆ your bubbly assistant ☆\n";

        String line = "✿═══════════════════════════════════════════════✿";

        Task[] tasks = new Task[100];
        int taskCount = 0;

        Storage storage = new Storage("./data/boba.txt");
        taskCount = storage.load(tasks);

        System.out.println(line);
        System.out.println(logo);
        System.out.println("    Hii! I'm Boba ◕‿◕");
        System.out.println("    What can I do for you today?");
        System.out.println(line);

        Scanner scanner = new Scanner(System.in);
        String input;

        while (true) {
            input = scanner.nextLine();

            if (input.equals("bye")) {
                break;
            }

            System.out.println(line);

            try {
                if (input.equals("list")) {
                    System.out.println("    Okie here's everything on your plate~ 🍡");
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println("    " + (i + 1) + "." + tasks[i]);
                    }
                } else if (input.startsWith("mark ")) {
                    int index = Integer.parseInt(input.substring(5)) - 1;
                    if (index < 0 || index >= taskCount) {
                        System.out.println("    Hmm that task doesn't exist!");
                        System.out.println("    You have " + taskCount + " task(s) btw~");
                    } else {
                        tasks[index].markAsDone();
                        storage.save(tasks, taskCount);
                        System.out.println("    Yay you did it!! ☆ﾟ.*･｡ﾟ");
                        System.out.println("    " + tasks[index]);
                    }
                } else if (input.startsWith("unmark ")) {
                    int index = Integer.parseInt(input.substring(7)) - 1;
                    if (index < 0 || index >= taskCount) {
                        System.out.println("    Hmm that task doesn't exist!");
                        System.out.println("    You have " + taskCount + " task(s) btw~");
                    } else {
                        tasks[index].markAsNotDone();
                        storage.save(tasks, taskCount);
                        System.out.println("    No worries, we all need more time sometimes~");
                        System.out.println("    " + tasks[index]);
                    }
                } else if (input.equals("todo") || input.equals("todo ")) {
                    System.out.println("    Uhh what's the task? Can't be empty~");
                    System.out.println("    Try: todo <description>");
                } else if (input.startsWith("todo ")) {
                    String description = input.substring(5).trim();
                    if (description.isEmpty()) {
                        System.out.println("    Uhh what's the task? Can't be empty~");
                        System.out.println("    Try: todo <description>");
                    } else {
                        tasks[taskCount] = new Todo(description);
                        taskCount++;
                        storage.save(tasks, taskCount);
                        System.out.println("    Got it! I've added this task ✿");
                        System.out.println("      " + tasks[taskCount - 1]);
                        System.out.println("    Now you have " + taskCount + " task(s) in the list~");
                    }
                } else if (input.equals("deadline") || input.equals("deadline ")) {
                    System.out.println("    A deadline needs a /by date!");
                    System.out.println("    Try: deadline <description> /by <when>");
                } else if (input.startsWith("deadline ")) {
                    String content = input.substring(9);
                    if (!content.contains(" /by ")) {
                        System.out.println("    When's it due? Add /by <date>~");
                        System.out.println("    Try: deadline <description> /by <when>");
                    } else {
                        String[] parts = content.split(" /by ");
                        String description = parts[0].trim();
                        String by = parts[1].trim();
                        if (description.isEmpty() || by.isEmpty()) {
                            System.out.println("    Hmm something's missing there~");
                            System.out.println("    Try: deadline <description> /by <when>");
                        } else {
                            tasks[taskCount] = new Deadline(description, by);
                            taskCount++;
                            storage.save(tasks, taskCount);
                            System.out.println("    Got it! I've added this task ✿");
                            System.out.println("      " + tasks[taskCount - 1]);
                            System.out.println("    Now you have " + taskCount + " task(s) in the list~");
                        }
                    }
                } else if (input.equals("event") || input.equals("event ")) {
                    System.out.println("    An event needs /from and /to times!");
                    System.out.println("    Try: event <description> /from <start> /to <end>");
                } else if (input.startsWith("event ")) {
                    String content = input.substring(6);
                    if (!content.contains(" /from ") || !content.contains(" /to ")) {
                        System.out.println("    I need both /from and /to times~");
                        System.out.println("    Try: event <description> /from <start> /to <end>");
                    } else {
                        String[] parts = content.split(" /from ");
                        String description = parts[0].trim();
                        String[] timeParts = parts[1].split(" /to ");
                        String from = timeParts[0].trim();
                        String to = timeParts[1].trim();
                        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
                            System.out.println("    Hmm something's missing there~");
                            System.out.println("    Try: event <description> /from <start> /to <end>");
                        } else {
                            tasks[taskCount] = new Event(description, from, to);
                            taskCount++;
                            storage.save(tasks, taskCount);
                            System.out.println("    Got it! I've added this task ✿");
                            System.out.println("      " + tasks[taskCount - 1]);
                            System.out.println("    Now you have " + taskCount + " task(s) in the list~");
                        }
                    }
                } else if (input.startsWith("delete ")) {
                    int index = Integer.parseInt(input.substring(7)) - 1;
                    if (index < 0 || index >= taskCount) {
                        System.out.println("    Hmm that task doesn't exist!");
                        System.out.println("    You have " + taskCount + " task(s) btw~");
                    } else {
                        Task removed = tasks[index];
                        for (int i = index; i < taskCount - 1; i++) {
                            tasks[i] = tasks[i + 1];
                        }
                        tasks[taskCount - 1] = null;
                        taskCount--;
                        storage.save(tasks, taskCount);
                        System.out.println("    Alright, I've removed this task~");
                        System.out.println("      " + removed);
                        System.out.println("    Now you have " + taskCount + " task(s) in the list.");
                    }
                } else if (input.equals("delete")) {
                    System.out.println("    Which task tho?");
                    System.out.println("    Try: delete <task number>");
                } else if (input.equals("mark") || input.equals("unmark")) {
                    System.out.println("    Which task tho?");
                    System.out.println("    Try: " + input + " <task number>");
                } else {
                    System.out.println("    Hmm I don't know that one~");
                    System.out.println("    Try: todo, deadline, event, list, mark, unmark, delete, or bye!");
                }
            } catch (NumberFormatException e) {
                System.out.println("    That's not a number silly~");
                System.out.println("    Try: mark <number> or unmark <number>");
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("    Hmm something's off with that format~");
                System.out.println("    Check your command and try again!");
            }

            System.out.println(line);
        }

        System.out.println(line);
        System.out.println("    Bye bye :) Hope to see you again soon! ♡");
        System.out.println(line);

        scanner.close();
    }
}
