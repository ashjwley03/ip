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

        // Greeting
        System.out.println(line);
        System.out.println(logo);
        System.out.println("    Hii! I'm Boba ◕‿◕");
        System.out.println("    What can I do for you today?");
        System.out.println(line);

        // Read and echo user input
        Scanner scanner = new Scanner(System.in);
        String input;

        while (true) {
            input = scanner.nextLine();

            if (input.equals("bye")) {
                break;
            }

            System.out.println(line);
            System.out.println("    " + input);
            System.out.println(line);
        }

        // Goodbye
        System.out.println(line);
        System.out.println("    Bye bye :) Hope to see you again soon! ♡");
        System.out.println(line);

        scanner.close();
    }
}
