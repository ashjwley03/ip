package boba.storage;

import boba.exception.BobaException;
import boba.task.Deadline;
import boba.task.Event;
import boba.task.Task;
import boba.task.TaskList;
import boba.task.Todo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StorageTest {

    @TempDir
    private File tempDir;

    private String getPath(String name) {
        return tempDir.getAbsolutePath() + "/" + name;
    }

    @Test
    public void load_nonExistentFile_returnsEmptyList()
            throws BobaException {
        Storage storage = new Storage(getPath("none.txt"));
        ArrayList<Task> tasks = storage.load();
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void saveAndLoad_roundTrip_preservesTasks()
            throws BobaException {
        String path = getPath("roundtrip.txt");
        Storage storage = new Storage(path);

        TaskList list = new TaskList();
        list.add(new Todo("buy boba"));
        list.add(new Deadline("homework", "2024-12-25"));
        list.add(new Event("meeting", "2pm", "4pm"));
        storage.save(list);

        ArrayList<Task> loaded = storage.load();
        assertEquals(3, loaded.size());
        assertTrue(loaded.get(0).toString().contains("buy boba"));
        assertTrue(loaded.get(1).toString().contains("homework"));
        assertTrue(loaded.get(2).toString().contains("meeting"));
    }

    @Test
    public void load_corruptedLines_skipsGracefully()
            throws BobaException, IOException {
        String path = getPath("corrupt.txt");
        FileWriter writer = new FileWriter(path);
        writer.write("T | 0 | valid task\n");
        writer.write("this is garbage\n");
        writer.write("D | 1 | homework | 2024-12-01\n");
        writer.write("\n");
        writer.write("also garbage | | |\n");
        writer.close();

        Storage storage = new Storage(path);
        ArrayList<Task> tasks = storage.load();
        assertEquals(2, tasks.size());
        assertTrue(tasks.get(0).toString().contains("valid task"));
        assertTrue(tasks.get(1).toString().contains("homework"));
    }

    @Test
    public void load_emptyFile_returnsEmptyList()
            throws BobaException, IOException {
        String path = getPath("empty.txt");
        new FileWriter(path).close();

        Storage storage = new Storage(path);
        ArrayList<Task> tasks = storage.load();
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void saveAndLoad_markedDone_preservesStatus()
            throws BobaException {
        String path = getPath("done.txt");
        Storage storage = new Storage(path);

        TaskList list = new TaskList();
        Todo todo = new Todo("done task");
        todo.markAsDone();
        list.add(todo);
        storage.save(list);

        ArrayList<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        assertTrue(loaded.get(0).isDone());
    }

    @Test
    public void saveAndLoad_recurringTask_preservesRecurrence()
            throws BobaException {
        String path = getPath("recur.txt");
        Storage storage = new Storage(path);

        TaskList list = new TaskList();
        Todo todo = new Todo("weekly standup");
        todo.setRecurrence("weekly");
        list.add(todo);
        storage.save(list);

        ArrayList<Task> loaded = storage.load();
        assertEquals(1, loaded.size());
        assertTrue(loaded.get(0).isRecurring());
        assertEquals("weekly", loaded.get(0).getRecurrence());
    }

    @Test
    public void load_createsDirectoryIfMissing() {
        String path = tempDir.getAbsolutePath()
                + "/subdir/deep/data.txt";
        Storage storage = new Storage(path);
        assertDoesNotThrow(storage::load);
    }
}
