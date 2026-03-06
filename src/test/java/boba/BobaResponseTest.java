package boba;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BobaResponseTest {

    @TempDir
    private File tempDir;

    private Boba boba;

    @BeforeEach
    public void setUp() {
        String tempPath = tempDir.getAbsolutePath() + "/test_boba.txt";
        boba = new Boba(tempPath);
    }

    @Test
    public void getResponse_todoCommand_addsTask() {
        String response = boba.getResponse("todo buy boba tea");
        assertTrue(response.contains("new pearl"));
        assertTrue(response.contains("buy boba tea"));
        assertTrue(response.contains("1 task(s)"));
    }

    @Test
    public void getResponse_deadlineCommand_addsDeadline() {
        String response = boba.getResponse("deadline homework /by 2024-12-25");
        assertTrue(response.contains("new pearl"));
        assertTrue(response.contains("homework"));
        assertTrue(response.contains("1 task(s)"));
    }

    @Test
    public void getResponse_listCommand_showsAllTasks() {
        boba.getResponse("todo task one");
        boba.getResponse("todo task two");

        String response = boba.getResponse("list");
        assertTrue(response.contains("task one"));
        assertTrue(response.contains("task two"));
    }

    @Test
    public void getResponse_markCommand_marksTask() {
        boba.getResponse("todo buy boba");
        String response = boba.getResponse("mark 1");
        assertTrue(response.contains("one less pearl"));
        assertTrue(response.contains("[X]"));
    }

    @Test
    public void getResponse_markInvalidIndex_showsError() {
        String response = boba.getResponse("mark 99");
        assertTrue(response.contains("can't find that pearl"));
    }

    @Test
    public void getResponse_unknownCommand_showsError() {
        String response = boba.getResponse("dance");
        assertTrue(response.contains("not on the menu"));
        assertTrue(response.contains("Try:"));
    }

    @Test
    public void getResponse_byeCommand_returnsFarewell() {
        String response = boba.getResponse("bye");
        assertTrue(response.contains("Bye bye"));
    }

    @Test
    public void getResponse_emptyTodo_showsError() {
        String response = boba.getResponse("todo");
        assertFalse(response.contains("Got it!"));
        assertTrue(response.contains("empty"));
    }

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

    @Test
    public void getResponse_findNoMatch_showsNoResults() {
        boba.getResponse("todo buy boba");
        String response = boba.getResponse("find pizza");
        assertTrue(response.contains("not in the cup"));
    }

    @Test
    public void getResponse_deleteCommand_removesTask() {
        boba.getResponse("todo buy boba");
        boba.getResponse("todo read book");

        String response = boba.getResponse("delete 1");
        assertTrue(response.contains("old tea leaves"));
        assertTrue(response.contains("buy boba"));
        assertTrue(response.contains("1 task(s)"));
    }

    @Test
    public void getResponse_cheerCommand_returnsQuote() {
        String response = boba.getResponse("cheer");
        assertFalse(response.isEmpty());
        assertTrue(response.contains("\uD83E\uDD64"));
    }
}
