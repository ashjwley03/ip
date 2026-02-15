package boba.task;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskListTest {

    // Positive: adding tasks should increase the size correctly
    @Test
    public void add_multipleTasks_sizeIncreases() {
        TaskList list = new TaskList();
        assertEquals(0, list.size());

        list.add(new Todo("buy boba"));
        assertEquals(1, list.size());

        list.add(new Deadline("homework", "2024-12-01"));
        list.add(new Event("meeting", "2pm", "4pm"));
        assertEquals(3, list.size());
    }

    // Positive: deleting a task should return it and decrease the size
    @Test
    public void delete_validIndex_returnsTaskAndDecrementsSize() {
        TaskList list = new TaskList();
        list.add(new Todo("buy boba"));
        list.add(new Todo("read book"));
        list.add(new Todo("do laundry"));

        Task removed = list.delete(1);
        assertTrue(removed.toString().contains("read book"));
        assertEquals(2, list.size());
    }

    // Negative: deleting at an out-of-bounds index should throw an exception
    @Test
    public void delete_invalidIndex_throwsException() {
        TaskList list = new TaskList();
        list.add(new Todo("only task"));

        assertThrows(IndexOutOfBoundsException.class, () -> list.delete(5));
        assertThrows(IndexOutOfBoundsException.class, () -> list.delete(-1));
    }

    // Positive: find should return tasks matching the keyword (case-insensitive)
    @Test
    public void find_matchingKeyword_returnsMatches() {
        TaskList list = new TaskList();
        list.add(new Todo("buy boba tea"));
        list.add(new Todo("read book"));
        list.add(new Todo("buy milk"));
        list.add(new Deadline("return book", "tomorrow"));

        ArrayList<Task> results = list.find("book");
        assertEquals(2, results.size());
        assertTrue(results.get(0).toString().contains("read book"));
        assertTrue(results.get(1).toString().contains("return book"));
    }

    // Positive: find should be case-insensitive
    @Test
    public void find_caseInsensitive_returnsMatches() {
        TaskList list = new TaskList();
        list.add(new Todo("Buy BOBA"));
        list.add(new Todo("buy boba"));

        ArrayList<Task> results = list.find("boba");
        assertEquals(2, results.size());
    }

    // Negative: find with no matching keyword should return an empty list
    @Test
    public void find_noMatch_returnsEmptyList() {
        TaskList list = new TaskList();
        list.add(new Todo("buy boba"));
        list.add(new Todo("read book"));

        ArrayList<Task> results = list.find("pizza");
        assertTrue(results.isEmpty());
    }

    // Positive: constructing with existing tasks should preserve them
    @Test
    public void constructor_withExistingTasks_preservesTasks() {
        ArrayList<Task> existing = new ArrayList<>();
        existing.add(new Todo("task one"));
        existing.add(new Todo("task two"));

        TaskList list = new TaskList(existing);
        assertEquals(2, list.size());
        assertTrue(list.get(0).toString().contains("task one"));
        assertTrue(list.get(1).toString().contains("task two"));
    }
}
