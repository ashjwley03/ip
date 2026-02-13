package boba.task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class DeadlineTest {

    @Test
    public void constructor_validDate_parsesCorrectly() {
        Deadline deadline = new Deadline("homework", "2024-12-25");
        assertTrue(deadline.toString().contains("Dec 25 2024"));
    }

    @Test
    public void constructor_invalidDate_storesAsString() {
        Deadline deadline = new Deadline("homework", "tomorrow");
        assertTrue(deadline.toString().contains("tomorrow"));
    }

    @Test
    public void constructor_differentDateFormats_handledCorrectly() {
        // Valid ISO format
        Deadline d1 = new Deadline("task1", "2024-01-15");
        assertTrue(d1.toString().contains("Jan 15 2024"));

        // Invalid format stored as string
        Deadline d2 = new Deadline("task2", "15/01/2024");
        assertTrue(d2.toString().contains("15/01/2024"));

        // Text description
        Deadline d3 = new Deadline("task3", "next Monday");
        assertTrue(d3.toString().contains("next Monday"));
    }

    @Test
    public void getByForStorage_validDate_returnsIsoFormat() {
        Deadline deadline = new Deadline("homework", "2024-12-25");
        assertEquals("2024-12-25", deadline.getByForStorage());
    }

    @Test
    public void getByForStorage_invalidDate_returnsOriginalString() {
        Deadline deadline = new Deadline("homework", "tomorrow evening");
        assertEquals("tomorrow evening", deadline.getByForStorage());
    }

    @Test
    public void toString_notDone_showsEmptyBracket() {
        Deadline deadline = new Deadline("submit report", "2024-06-01");
        assertTrue(deadline.toString().contains("[ ]"));
        assertFalse(deadline.toString().contains("[X]"));
    }

    @Test
    public void toString_markedDone_showsX() {
        Deadline deadline = new Deadline("submit report", "2024-06-01");
        deadline.markAsDone();
        assertTrue(deadline.toString().contains("[X]"));
    }

    @Test
    public void toString_format_correctStructure() {
        Deadline deadline = new Deadline("buy milk", "2024-03-15");
        String result = deadline.toString();
        // Should be: [D][ ] buy milk (by: Mar 15 2024)
        assertTrue(result.startsWith("[D]"));
        assertTrue(result.contains("buy milk"));
        assertTrue(result.contains("(by:"));
        assertTrue(result.contains("Mar 15 2024"));
    }

    @Test
    public void markAsDone_thenUnmark_statusChanges() {
        Deadline deadline = new Deadline("test task", "soon");
        
        // Initially not done
        assertTrue(deadline.toString().contains("[ ]"));
        
        // Mark as done
        deadline.markAsDone();
        assertTrue(deadline.toString().contains("[X]"));
        
        // Unmark
        deadline.markAsNotDone();
        assertTrue(deadline.toString().contains("[ ]"));
    }
}
