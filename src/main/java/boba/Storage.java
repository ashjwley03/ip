package boba;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Storage {
    private String filePath;

    public Storage(String filePath) {
        this.filePath = filePath;
    }

    public ArrayList<Task> load() throws BobException {
        ArrayList<Task> tasks = new ArrayList<>();
        File file = new File(filePath);

        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        if (!file.exists()) {
            return tasks;
        }

        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                Task task = parseTask(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
            scanner.close();
        } catch (IOException e) {
            throw new BobException("Hmm couldn't load saved tasks~");
        }

        return tasks;
    }

    private Task parseTask(String line) {
        try {
            String[] parts = line.split(" \\| ");
            String type = parts[0];
            boolean isDone = parts[1].equals("1");
            String description = parts[2];

            Task task = null;
            switch (type) {
                case "T":
                    task = new Todo(description);
                    break;
                case "D":
                    task = new Deadline(description, parts[3]);
                    break;
                case "E":
                    task = new Event(description, parts[3], parts[4]);
                    break;
            }

            if (task != null && isDone) {
                task.markAsDone();
            }
            return task;
        } catch (Exception e) {
            return null;
        }
    }

    public void save(TaskList tasks) {
        try {
            File file = new File(filePath);

            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            FileWriter writer = new FileWriter(file);
            for (int i = 0; i < tasks.size(); i++) {
                writer.write(taskToString(tasks.get(i)) + "\n");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("    Oops, couldn't save tasks~");
        }
    }

    private String taskToString(Task task) {
        if (task instanceof Todo) {
            return "T | " + (task.isDone ? "1" : "0") + " | " + task.description;
        } else if (task instanceof Deadline) {
            Deadline d = (Deadline) task;
            return "D | " + (task.isDone ? "1" : "0") + " | " + task.description + " | " + d.getByForStorage();
        } else if (task instanceof Event) {
            Event e = (Event) task;
            return "E | " + (task.isDone ? "1" : "0") + " | " + task.description + " | " + e.from + " | " + e.to;
        }
        return "";
    }
}
