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
        String[] tasks = new String[100];
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
                System.out.println("    Here's your list! ✿");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println("    " + (i + 1) + ". " + tasks[i]);
                }
            } else {
                tasks[taskCount] = input;
                taskCount++;
                System.out.println("    added: " + input);
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
