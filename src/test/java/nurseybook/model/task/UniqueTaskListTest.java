package nurseybook.model.task;

import static nurseybook.testutil.Assert.assertThrows;
import static nurseybook.testutil.TypicalTasks.ALICE_INSULIN;
import static nurseybook.testutil.TypicalTasks.APPLY_LEAVE;
import static nurseybook.testutil.TypicalTasks.APPLY_LEAVE_DAY_NEXT_RECURRENCE_GHOST;
import static nurseybook.testutil.TypicalTasks.APPLY_LEAVE_LATE_TIME;
import static nurseybook.testutil.TypicalTasks.APPLY_LEAVE_MONTH_NEXT_RECURRENCE_GHOST;
import static nurseybook.testutil.TypicalTasks.APPLY_LEAVE_MONTH_RECURRENCE;
import static nurseybook.testutil.TypicalTasks.APPLY_LEAVE_WEEK_NEXT_RECURRENCE_GHOST;
import static nurseybook.testutil.TypicalTasks.APPLY_LEAVE_WEEK_RECURRENCE;
import static nurseybook.testutil.TypicalTasks.DO_PAPERWORK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import nurseybook.model.task.exceptions.TaskNotFoundException;
import nurseybook.testutil.TaskBuilder;

public class UniqueTaskListTest {
    private final UniqueTaskList uniqueTaskList = new UniqueTaskList();

    @Test
    public void contains_nullTask_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueTaskList.contains(null));
    }

    @Test
    public void has_taskNotInList_returnsFalse() {
        assertFalse(uniqueTaskList.contains(APPLY_LEAVE));
    }

    @Test
    public void contains_taskInList_returnsTrue() {
        uniqueTaskList.add(APPLY_LEAVE);
        assertTrue(uniqueTaskList.contains(APPLY_LEAVE));
    }

    @Test
    public void add_nullTask_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueTaskList.add(null));
    }

    @Test
    public void remove_nullTask_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueTaskList.remove(null));
    }

    @Test
    public void remove_taskDoesNotExist_throwsTaskNotFoundException() {
        assertThrows(TaskNotFoundException.class, () -> uniqueTaskList.remove(DO_PAPERWORK));
    }

    @Test
    public void remove_existingTask_removesTask() {
        uniqueTaskList.add(DO_PAPERWORK);
        uniqueTaskList.remove(DO_PAPERWORK);
        UniqueTaskList expectedUniqueTaskList = new UniqueTaskList();
        assertEquals(expectedUniqueTaskList, uniqueTaskList);
    }

    @Test
    public void mark_nullTask_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueTaskList.markTaskAsDone(null));
        assertThrows(NullPointerException.class, () -> uniqueTaskList.markTaskAsOverdue(null));
        assertThrows(NullPointerException.class, () -> uniqueTaskList.markTaskAsNotOverdue(null));
    }

    @Test
    public void mark_taskDoesNotExist_throwsTaskNotFoundException() {
        assertThrows(TaskNotFoundException.class, () -> uniqueTaskList.markTaskAsDone(DO_PAPERWORK));
        assertThrows(TaskNotFoundException.class, () -> uniqueTaskList.markTaskAsOverdue(DO_PAPERWORK));
        assertThrows(TaskNotFoundException.class, () -> uniqueTaskList.markTaskAsNotOverdue(DO_PAPERWORK));
    }

    @Test
    public void setTasks_nullTaskList_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueTaskList.setTasks((UniqueTaskList) null));
    }

    @Test
    public void setTasks_taskList_replacesOwnListWithProvidedTaskList() {
        uniqueTaskList.add(APPLY_LEAVE);
        UniqueTaskList expectedUniqueTaskList = new UniqueTaskList();
        expectedUniqueTaskList.add(DO_PAPERWORK);
        uniqueTaskList.setTasks(expectedUniqueTaskList);
        assertEquals(expectedUniqueTaskList, uniqueTaskList);
    }

    @Test
    public void setTasks_nullList_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueTaskList.setTasks((List<Task>) null));
    }

    @Test
    public void setTasks_list_replacesOwnListWithProvidedList() {
        uniqueTaskList.add(APPLY_LEAVE);
        List<Task> tList = Collections.singletonList(DO_PAPERWORK);
        uniqueTaskList.setTasks(tList);
        UniqueTaskList expectedUniqueTaskList = new UniqueTaskList();
        expectedUniqueTaskList.add(DO_PAPERWORK);
        assertEquals(expectedUniqueTaskList, uniqueTaskList);
    }

    @Test
    public void sortsAddedTasks_byDateTime() {
        uniqueTaskList.add(ALICE_INSULIN); // date: "2022-01-31", time: "19:45"
        uniqueTaskList.add(DO_PAPERWORK); // date: "2022-01-31", time: "10:20"
        uniqueTaskList.add(APPLY_LEAVE); // date: "2021-10-01", time: "00:00"
        UniqueTaskList expectedUniqueTaskList = new UniqueTaskList();
        expectedUniqueTaskList.add(APPLY_LEAVE);
        expectedUniqueTaskList.add(DO_PAPERWORK);
        expectedUniqueTaskList.add(ALICE_INSULIN);
        assertEquals(expectedUniqueTaskList, uniqueTaskList);
    }

    @Test
    public void updateDateOfRecurringTask_nullTask_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueTaskList.updateDateOfRecurringTask(null));
    }

    @Test
    public void updateDateOfRecurringTask_taskList_updatesPastDateWithNewDate() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        Task t = APPLY_LEAVE_LATE_TIME;
        uniqueTaskList.add(APPLY_LEAVE_LATE_TIME);
        uniqueTaskList.updateDateOfRecurringTask(t);
        Task test = new TaskBuilder(APPLY_LEAVE_LATE_TIME)
                        .withDateTime(currentDateTime.toLocalDate().toString(),
                            t.getTime().toString()).withStatus("false", "false").build();
        assertTrue(uniqueTaskList.contains(test));
    }

    @Test
    public void deleteGhostTasks_deletesAllGhostTasks() {
        UniqueTaskList uniqueTaskList = new UniqueTaskList();
        uniqueTaskList.add(APPLY_LEAVE_DAY_NEXT_RECURRENCE_GHOST);
        uniqueTaskList.add(APPLY_LEAVE);
        uniqueTaskList.add(APPLY_LEAVE_WEEK_NEXT_RECURRENCE_GHOST);
        uniqueTaskList.add(APPLY_LEAVE_MONTH_NEXT_RECURRENCE_GHOST);
        uniqueTaskList.deleteGhostTasks();

        UniqueTaskList expectedUniqueTaskList = new UniqueTaskList();
        expectedUniqueTaskList.add(APPLY_LEAVE);

        assertEquals(uniqueTaskList, expectedUniqueTaskList);
    }

    @Test
    public void addPossibleGhostTasksWithMatchingDate_forDayRecurring() {
        UniqueTaskList taskList = new UniqueTaskList();
        taskList.add(APPLY_LEAVE_LATE_TIME); // date: "2021-10-01"
        LocalDate keyDate = LocalDate.parse("2021-10-02"); //next day date

        taskList.addPossibleGhostTasksWithMatchingDate(keyDate);

        UniqueTaskList expectedTaskList = new UniqueTaskList();
        expectedTaskList.add(APPLY_LEAVE_LATE_TIME);
        expectedTaskList.add(APPLY_LEAVE_DAY_NEXT_RECURRENCE_GHOST);

        assertEquals(taskList, expectedTaskList);
    }

    @Test
    public void addPossibleGhostTasksWithMatchingDate_forWeekRecurring() {
        UniqueTaskList taskList = new UniqueTaskList();
        taskList.add(APPLY_LEAVE_WEEK_RECURRENCE); // date: "2021-09-30"
        LocalDate keyDate = LocalDate.parse ("2021-10-07"); //next week date

        taskList.addPossibleGhostTasksWithMatchingDate(keyDate);

        UniqueTaskList expectedTaskList = new UniqueTaskList();
        expectedTaskList.add(APPLY_LEAVE_WEEK_RECURRENCE);
        expectedTaskList.add(APPLY_LEAVE_WEEK_NEXT_RECURRENCE_GHOST);

        assertEquals(taskList, expectedTaskList);
    }

    @Test
    public void addPossibleGhostTasksWithMatchingDate_forMonthRecurring() {
        UniqueTaskList taskList = new UniqueTaskList();
        taskList.add(APPLY_LEAVE_MONTH_RECURRENCE); // date: "2021-08-27"
        LocalDate keyDate = LocalDate.parse("2021-08-27"); //next week date

        taskList.addPossibleGhostTasksWithMatchingDate(keyDate);

        UniqueTaskList expectedTaskList = new UniqueTaskList();
        expectedTaskList.add(APPLY_LEAVE_MONTH_RECURRENCE);
        expectedTaskList.add(APPLY_LEAVE_MONTH_NEXT_RECURRENCE_GHOST);

        assertEquals(taskList, expectedTaskList);
    }

}