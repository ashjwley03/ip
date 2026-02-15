package boba;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BobResponseTest {

    @TempDir
    File tempDir;

    private Boba boba;

    @BeforeEach
    public void setUp() {
        // Use a temp directory so tests don't interfere with real data
        String tempPath = tempDir.getAbsolutePath() + "/test_boba.txt";
        boba = new Boba(tempPath);
    }

    // Positive: adding a todo should confirm the task was added
    @Test
    public void getResponse_todoCommand_addsTask() {
        String response = boba.getResponse("todo buy boba tea");
        assertTrue(response.contains("Got it!"));
        assertTrue(response.contains("buy boba tea"));
        assertTrue(response.contains("1 task(s)"));
    }

    // Positive: adding a deadline should include task details in response
    @Test
    public void getResponse_deadlineCommand_addsDeadline() {
        String response = boba.getResponse("deadline homework /by 2024-12-25");
        assertTrue(response.contains("Got it!"));
        assertTrue(response.contains("homework"));
        assertTrue(response.contains("1 task(s)"));
    }

    // Positive: listing tasks should show all added tasks
    @Test
    public void getResponse_listCommand_showsAllTasks() {
        boba.getResponse("todo task one");
        boba.getResponse("todo task two");

        String response = boba.getResponse("list");
        assertTrue(response.contains("task one"));
        assertTrue(response.contains("task two"));
    }

    // Positive: marking a task should confirm it was completed
    @Test
    public void getResponse_markCommand_marksTask() {
        boba.getResponse("todo buy boba");
        String response = boba.getResponse("mark 1");
        assertTrue(response.contains("Yay"));
        assertTrue(response.contains("[X]"));
    }

    // Negative: marking an invalid index should show error
    @Test
    public void getResponse_markInvalidIndex_showsError() {
        String response = boba.getResponse("mark 99");
        assertTrue(response.contains("doesn't exist"));
    }

    // Negative: an unknown command should show a helpful error
    @Test
    public void getResponse_unknownCommand_showsError() {
        String response = boba.getResponse("dance");
        assertTrue(response.contains("don't know that one"));
        assertTrue(response.contains("Try:"));
    }

    // Positive: bye command should return a farewell message
    @Test
    public void getResponse_byeCommand_returnsFarewell() {
        String response = boba.getResponse("bye");
        assertTrue(response.contains("Bye bye"));
    }

    // Negative: todo with empty description should show error
    @Test
    public void getResponse_emptyTodo_showsError() {
        String response = boba.getResponse("todo");
        assertFalse(response.contains("Got it!"));
        assertTrue(response.contains("empty"));
    }

    // Positive: find should return matching tasks
    @Test
    public void getResponse_findCommand_returnsMatches() {
        boba.getResponse("todo read book");
        boba.getResponse("todo buy boba");
        boba.getResponse("todo return book");

        String response = boba.getResponse("find book");
        assertTrue(response.contains("read book"));
        assertTrue(response.contains("return book"));
        assertFalse(response.contains("buy boba"));
    }

    // Negative: find with no matches should say so
    @Test
    public void getResponse_findNoMatch_showsNoResults() {
        boba.getResponse("todo buy boba");
        String response = boba.getResponse("find pizza");
        assertTrue(response.contains("no tasks match"));
    }

    // Positive: delete should remove a task and confirm
    @Test
    public void getResponse_deleteCommand_removesTask() {
        boba.getResponse("todo buy boba");
        boba.getResponse("todo read book");

        String response = boba.getResponse("delete 1");
        assertTrue(response.contains("removed"));
        assertTrue(response.contains("buy boba"));
        assertTrue(response.contains("1 task(s)"));
    }

    // Positive: cheer command should return a motivational quote
    @Test
    public void getResponse_cheerCommand_returnsQuote() {
        String response = boba.getResponse("cheer");
        assertFalse(response.isEmpty());
        assertTrue(response.contains("\u2728")); // sparkle emoji ✨
    }
}
