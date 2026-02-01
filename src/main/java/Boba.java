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

        // Task storage
        Task[] tasks = new Task[100];
        int taskCount = 0;

        // Greeting
        System.out.println(line);
        System.out.println(logo);
        System.out.println("    Hii! I'm Boba ◕‿◕");
        System.out.println("    What can I do for you today?");
        System.out.println(line);

        // Read and process user input
        Scanner scanner = new Scanner(System.in);
        String input;

        while (true) {
            input = scanner.nextLine();

            if (input.equals("bye")) {
                break;
            }

            System.out.println(line);

            if (input.equals("list")) {
                System.out.println("    Okie here's everything on your plate~ 🍡");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println("    " + (i + 1) + "." + tasks[i]);
                }
            } else if (input.startsWith("mark ")) {
                int index = Integer.parseInt(input.substring(5)) - 1;
                tasks[index].markAsDone();
                System.out.println("    Yay you did it!! ☆ﾟ.*･｡ﾟ");
                System.out.println("    " + tasks[index]);
            } else if (input.startsWith("unmark ")) {
                int index = Integer.parseInt(input.substring(7)) - 1;
                tasks[index].markAsNotDone();
                System.out.println("    No worries, we all need more time sometimes~");
                System.out.println("    " + tasks[index]);
            } else if (input.startsWith("todo ")) {
                String description = input.substring(5);
                tasks[taskCount] = new Todo(description);
                taskCount++;
                System.out.println("    Got it! I've added this task ✿");
                System.out.println("      " + tasks[taskCount - 1]);
                System.out.println("    Now you have " + taskCount + " task(s) in the list~");
            } else if (input.startsWith("deadline ")) {
                String[] parts = input.substring(9).split(" /by ");
                String description = parts[0];
                String by = parts[1];
                tasks[taskCount] = new Deadline(description, by);
                taskCount++;
                System.out.println("    Got it! I've added this task ✿");
                System.out.println("      " + tasks[taskCount - 1]);
                System.out.println("    Now you have " + taskCount + " task(s) in the list~");
            } else if (input.startsWith("event ")) {
                String[] parts = input.substring(6).split(" /from ");
                String description = parts[0];
                String[] timeParts = parts[1].split(" /to ");
                String from = timeParts[0];
                String to = timeParts[1];
                tasks[taskCount] = new Event(description, from, to);
                taskCount++;
                System.out.println("    Got it! I've added this task ✿");
                System.out.println("      " + tasks[taskCount - 1]);
                System.out.println("    Now you have " + taskCount + " task(s) in the list~");
            } else {
                System.out.println("    " + input);
            }

            System.out.println(line);
        }

        // Goodbye
        System.out.println(line);
        System.out.println("    Bye bye :) Hope to see you again soon! ♡");
        System.out.println(line);

        scanner.close();
    }
}
