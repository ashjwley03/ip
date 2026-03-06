package boba;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BobaFeatureTest {

    @TempDir
    private File tempDir;

    private Boba boba;

    @BeforeEach
    public void setUp() {
        String tempPath = tempDir.getAbsolutePath()
                + "/test_boba.txt";
        boba = new Boba(tempPath);
    }

    @Test
    public void getResponse_blankInput_showsMessage() {
        String response = boba.getResponse("   ");
        assertTrue(response.contains("blank"));
    }

    @Test
    public void getResponse_eventCommand_addsEvent() {
        String response = boba.getResponse(
                "event party /from 2pm /to 4pm");
        assertTrue(response.contains("new pearl"));
        assertTrue(response.contains("party"));
        assertTrue(response.contains("2pm"));
    }

    @Test
    public void getResponse_doafterCommand_addsTask() {
        String response = boba.getResponse(
                "doafter return book /after exam");
        assertTrue(response.contains("new pearl"));
        assertTrue(response.contains("return book"));
        assertTrue(response.contains("exam"));
    }

    @Test
    public void getResponse_dowithinCommand_addsTask() {
        String response = boba.getResponse(
                "dowithin collect cert /between Jan 15 /and Jan 25");
        assertTrue(response.contains("new pearl"));
        assertTrue(response.contains("collect cert"));
    }

    @Test
    public void getResponse_fixedCommand_addsTask() {
        String response = boba.getResponse(
                "fixed read report /needs 2 hours");
        assertTrue(response.contains("new pearl"));
        assertTrue(response.contains("read report"));
    }

    @Test
    public void getResponse_bobaCommand_recommends() {
        String response = boba.getResponse("boba");
        assertTrue(response.contains("recommend"));
        assertTrue(response.contains("\uD83E\uDD64"));
    }

    @Test
    public void getResponse_unmarkCommand_unmarksTask() {
        boba.getResponse("todo test task");
        boba.getResponse("mark 1");
        String response = boba.getResponse("unmark 1");
        assertTrue(response.contains("steep"));
        assertTrue(response.contains("[ ]"));
    }

    @Test
    public void getResponse_deleteInvalidIndex_showsError() {
        String response = boba.getResponse("delete 99");
        assertTrue(response.contains("can't find that pearl"));
    }

    @Test
    public void getResponse_deadlineInvalidDate_showsError() {
        String response = boba.getResponse(
                "deadline hw /by 2024-02-30");
        assertTrue(response.contains("not a valid date"));
    }

    @Test
    public void getResponse_eventStartAfterEnd_showsError() {
        String response = boba.getResponse(
                "event party /from 2024-12-25 /to 2024-12-20");
        assertTrue(response.contains("after"));
    }

    @Test
    public void getResponse_pipeInInput_showsError() {
        String response = boba.getResponse("todo buy | sell");
        assertTrue(response.contains("|"));
    }

    @Test
    public void getResponse_duplicateTask_showsWarning() {
        boba.getResponse("todo buy boba");
        String response = boba.getResponse("todo buy boba");
        assertTrue(response.contains("Duplicate"));
    }

    @Test
    public void getResponse_pastDeadline_showsWarning() {
        String response = boba.getResponse(
                "deadline old task /by 2020-01-01");
        assertTrue(response.contains("already past"));
    }

    @Test
    public void getResponse_scheduleCommand_showsSchedule() {
        String response = boba.getResponse("schedule");
        assertTrue(response.contains("Schedule for"));
    }

    @Test
    public void getResponse_remindCommand_showsReminders() {
        String response = boba.getResponse("remind");
        assertFalse(response.isEmpty());
    }

    @Test
    public void getResponse_extraWhitespace_worksNormally() {
        String response = boba.getResponse("  todo   buy  milk  ");
        assertTrue(response.contains("new pearl"));
        assertTrue(response.contains("buy milk"));
    }

    @Test
    public void getResponse_caseInsensitiveCommand_works() {
        String response = boba.getResponse("LIST");
        assertTrue(response.contains("brewing"));
    }

    @Test
    public void getResponse_markThenList_showsCompleted() {
        boba.getResponse("todo first task");
        boba.getResponse("todo second task");
        boba.getResponse("mark 1");
        String response = boba.getResponse("list");
        assertTrue(response.contains("[X]"));
        assertTrue(response.contains("first task"));
        assertTrue(response.contains("second task"));
    }

    @Test
    public void getResponse_snoozeDeadline_reschedulesTask() {
        boba.getResponse("deadline hw /by 2025-06-01");
        String response = boba.getResponse(
                "snooze 1 /to 2025-07-01");
        assertTrue(response.contains("brew time"));
    }

    @Test
    public void getResponse_findEmptyKeyword_showsError() {
        String response = boba.getResponse("find");
        assertTrue(response.contains("flavor"));
    }
}
