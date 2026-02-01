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
        boolean[] isDone = new boolean[100];
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
                    String status = isDone[i] ? "[X]" : "[ ]";
                    System.out.println("    " + (i + 1) + "." + status + " " + tasks[i]);
                }
            } else if (input.startsWith("mark ")) {
                int index = Integer.parseInt(input.substring(5)) - 1;
                isDone[index] = true;
                System.out.println("    Yay you did it!! ☆ﾟ.*･｡ﾟ");
                System.out.println("    [X] " + tasks[index]);
            } else if (input.startsWith("unmark ")) {
                int index = Integer.parseInt(input.substring(7)) - 1;
                isDone[index] = false;
                System.out.println("    No worries, we all need more time sometimes~");
                System.out.println("    [ ] " + tasks[index]);
            } else {
                tasks[taskCount] = input;
                isDone[taskCount] = false;
                taskCount++;
                System.out.println("    Got it! I've added this to your list 📝");
                System.out.println("    " + tasks[taskCount - 1]);
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
