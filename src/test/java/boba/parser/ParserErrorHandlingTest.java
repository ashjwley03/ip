package boba.parser;

import boba.exception.BobaException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParserErrorHandlingTest {

    @Test
    public void getCommand_leadingSpaces_trimmed() {
        assertEquals("todo", Parser.getCommand("   todo buy milk"));
    }

    @Test
    public void getCommand_upperCase_lowered() {
        assertEquals("todo", Parser.getCommand("TODO buy milk"));
        assertEquals("deadline", Parser.getCommand("Deadline hw /by mon"));
    }

    @Test
    public void getArguments_multipleSpaces_normalized() {
        assertEquals("buy milk", Parser.getArguments(
                "todo   buy   milk"));
    }

    @Test
    public void parseIndex_zeroIndex_throwsException() {
        assertThrows(NumberFormatException.class,
                () -> Parser.parseIndex("mark 0"));
    }

    @Test
    public void parseIndex_negativeIndex_throwsException() {
        assertThrows(NumberFormatException.class,
                () -> Parser.parseIndex("mark -1"));
    }

    @Test
    public void parseTodo_pipeCharacter_throwsBobaException() {
        BobaException ex = assertThrows(BobaException.class,
                () -> Parser.parseTodo("buy | sell"));
        assertTrue(ex.getMessage().contains("|"));
    }

    @Test
    public void parseTodo_whitespaceOnly_throwsBobaException() {
        assertThrows(BobaException.class,
                () -> Parser.parseTodo("   "));
    }

    @Test
    public void parseDeadline_invalidDate_throwsBobaException() {
        BobaException ex = assertThrows(BobaException.class,
                () -> Parser.parseDeadline("hw /by 2024-02-30"));
        assertTrue(ex.getMessage().contains("not a valid date"));
    }

    @Test
    public void parseDeadline_duplicateBy_throwsBobaException() {
        BobaException ex = assertThrows(BobaException.class,
                () -> Parser.parseDeadline(
                        "hw /by mon /by tue"));
        assertTrue(ex.getMessage().contains("more than once"));
    }

    @Test
    public void parseDeadline_validStringDate_doesNotThrow() {
        assertDoesNotThrow(
                () -> Parser.parseDeadline("hw /by tomorrow"));
    }

    @Test
    public void parseEvent_startAfterEnd_throwsBobaException() {
        BobaException ex = assertThrows(BobaException.class,
                () -> Parser.parseEvent(
                        "party /from 2024-12-25 /to 2024-12-20"));
        assertTrue(ex.getMessage().contains("after"));
    }

    @Test
    public void parseEvent_invalidFromDate_throwsBobaException() {
        BobaException ex = assertThrows(BobaException.class,
                () -> Parser.parseEvent(
                        "party /from 2024-13-01 /to 2024-12-25"));
        assertTrue(ex.getMessage().contains("not a valid date"));
    }

    @Test
    public void parseEvent_duplicateFrom_throwsBobaException() {
        BobaException ex = assertThrows(BobaException.class,
                () -> Parser.parseEvent(
                        "meet /from 2pm /from 3pm /to 5pm"));
        assertTrue(ex.getMessage().contains("more than once"));
    }

    @Test
    public void parseEvent_extraSpaces_succeeds() {
        assertDoesNotThrow(
                () -> Parser.parseEvent(
                        "  meeting  /from  2pm  /to  4pm  "));
    }

    @Test
    public void parseDoWithin_endBeforeStart_throwsBobaException() {
        BobaException ex = assertThrows(BobaException.class,
                () -> Parser.parseDoWithin(
                        "cert /between 2024-12-25"
                        + " /and 2024-12-01"));
        assertTrue(ex.getMessage().contains("after"));
    }

    @Test
    public void parseSnoozeDeadline_invalidDate_throwsBobaException() {
        BobaException ex = assertThrows(BobaException.class,
                () -> Parser.parseSnoozeDeadline(
                        "1 /to 2024-02-30"));
        assertTrue(ex.getMessage().contains("not a valid date"));
    }

    @Test
    public void parseSnoozeDeadline_nonNumericIndex_throwsBobaException() {
        BobaException ex = assertThrows(BobaException.class,
                () -> Parser.parseSnoozeDeadline("abc /to 2024-12-25"));
        assertTrue(ex.getMessage().contains("not a valid task number"));
    }

    @Test
    public void parseConfirm_missingSlot_throwsBobaException() {
        assertThrows(BobaException.class,
                () -> Parser.parseConfirm("1"));
    }

    @Test
    public void parseTentative_onlyOneSlot_throwsBobaException() {
        BobaException ex = assertThrows(BobaException.class,
                () -> Parser.parseTentative(
                        "meeting /slot 2pm - 4pm"));
        assertTrue(ex.getMessage().contains("at least 2"));
    }

    @Test
    public void parseFixedDuration_missingNeeds_throwsBobaException() {
        assertThrows(BobaException.class,
                () -> Parser.parseFixedDuration("read report"));
    }
}
