package boba;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParserTest {

    @Test
    public void getCommand_singleWord_returnsWord() {
        assertEquals("list", Parser.getCommand("list"));
    }

    @Test
    public void getCommand_multipleWords_returnsFirstWord() {
        assertEquals("todo", Parser.getCommand("todo buy milk"));
        assertEquals("mark", Parser.getCommand("mark 1"));
        assertEquals("deadline", Parser.getCommand("deadline homework /by 2024-12-01"));
    }

    @Test
    public void getArguments_noArguments_returnsEmpty() {
        assertEquals("", Parser.getArguments("list"));
        assertEquals("", Parser.getArguments("bye"));
    }

    @Test
    public void getArguments_withArguments_returnsArguments() {
        assertEquals("buy milk", Parser.getArguments("todo buy milk"));
        assertEquals("1", Parser.getArguments("mark 1"));
        assertEquals("homework /by tomorrow", Parser.getArguments("deadline homework /by tomorrow"));
    }

    @Test
    public void getArguments_extraSpaces_trimmed() {
        assertEquals("buy milk", Parser.getArguments("todo    buy milk"));
    }

    @Test
    public void parseIndex_validIndex_returnsZeroBased() {
        assertEquals(0, Parser.parseIndex("mark 1"));
        assertEquals(4, Parser.parseIndex("delete 5"));
        assertEquals(99, Parser.parseIndex("unmark 100"));
    }

    @Test
    public void parseIndex_noIndex_throwsException() {
        assertThrows(NumberFormatException.class, () -> Parser.parseIndex("mark"));
    }

    @Test
    public void parseIndex_invalidNumber_throwsException() {
        assertThrows(NumberFormatException.class, () -> Parser.parseIndex("mark abc"));
    }

    @Test
    public void parseTodo_validDescription_returnsTodo() throws BobException {
        Todo todo = Parser.parseTodo("buy boba");
        assertEquals("[T][ ] buy boba", todo.toString());
    }

    @Test
    public void parseTodo_emptyDescription_throwsBobException() {
        BobException exception = assertThrows(BobException.class, () -> Parser.parseTodo(""));
        assertTrue(exception.getMessage().contains("empty"));
    }

    @Test
    public void parseDeadline_validInput_returnsDeadline() throws BobException {
        Deadline deadline = Parser.parseDeadline("homework /by tomorrow");
        assertTrue(deadline.toString().contains("[D]"));
        assertTrue(deadline.toString().contains("homework"));
        assertTrue(deadline.toString().contains("tomorrow"));
    }

    @Test
    public void parseDeadline_missingBy_throwsBobException() {
        BobException exception = assertThrows(BobException.class, 
            () -> Parser.parseDeadline("homework tomorrow"));
        assertTrue(exception.getMessage().contains("/by"));
    }

    @Test
    public void parseDeadline_emptyDescription_throwsBobException() {
        assertThrows(BobException.class, () -> Parser.parseDeadline(" /by tomorrow"));
    }

    @Test
    public void parseEvent_validInput_returnsEvent() throws BobException {
        Event event = Parser.parseEvent("meeting /from 2pm /to 4pm");
        assertTrue(event.toString().contains("[E]"));
        assertTrue(event.toString().contains("meeting"));
        assertTrue(event.toString().contains("2pm"));
        assertTrue(event.toString().contains("4pm"));
    }

    @Test
    public void parseEvent_missingFrom_throwsBobException() {
        assertThrows(BobException.class, () -> Parser.parseEvent("meeting /to 4pm"));
    }

    @Test
    public void parseEvent_missingTo_throwsBobException() {
        assertThrows(BobException.class, () -> Parser.parseEvent("meeting /from 2pm"));
    }
}
