package nurseybook.model.task;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import nurseybook.model.NurseyBook;
import nurseybook.model.person.Elderly;
import nurseybook.model.person.Name;
import nurseybook.model.task.Recurrence.RecurrenceType;

public abstract class Task implements Comparable<Task> {

    private final Description desc;
    private DateTime dateTime;
    private final Status status;
    private final Set<Name> relatedNames = new HashSet<>();
    private final Recurrence recurrence;

    /**
     * Creates a Task object.
     *
     * @param desc                      the description of the task
     * @param dt                        the date and time of the task
     * @param names                     the names of people associated with the task
     * @param recurrence                the recurrence type of the task
     */
    public Task(Description desc, DateTime dt, Set<Name> names, Recurrence recurrence) {
        boolean isOverdue = DateTime.isOverdue(dt);

        this.desc = desc;
        this.dateTime = dt;
        this.relatedNames.addAll(names);
        this.status = new Status("false", Boolean.toString(isOverdue));
        this.recurrence = recurrence;
    }

    /**
     * Creates a Task object.
     *
     * @param desc                      the description of the task
     * @param dt                        the date and time of the task
     * @param names                     the names of people associated with the task
     * @param status                    the completion status of the task
     * @param recurrence                the recurrence type of the task
     */
    public Task(Description desc, DateTime dt, Set<Name> names, Status status, Recurrence recurrence) {
        this.desc = desc;
        this.dateTime = dt;
        this.relatedNames.addAll(names);
        this.status = status;
        this.recurrence = recurrence;
    }

    /**
     * Marks task as done.
     *
     * @return A new duplicate task object, except that it is marked as done
     */
    public abstract Task markAsDone();

    /**
     * Marks task as overdue.
     *
     * @return A new duplicate task object, except that it is marked as overdue
     */
    public abstract Task markAsOverdue();

    /**
     * Resets the overdue status of the task.
     *
     * @return A new duplicate task object, except that it is marked as undone and not overdue
     */
    public abstract Task markAsNotOverdue();

    /**
     * Updates the date of the recurring task such that it is not overdue.
     *
     * @return A new duplicate task object, except with a date in the future
     */
    public abstract Task updateDateRecurringTask();

    /**
     * Checks if this task is a recurring task.
     *
     * @return True if the task is a recurring task; false otherwise.
     */
    public boolean isTaskRecurring() {
        return this.recurrence.isRecurring();
    }

    /**
     * Checks if this is a real task.
     *
     * @return True if the task is a real task; false if it is a ghost task.
     */
    public boolean isRealTask() {
        return (this instanceof RealTask);
    }

    /**
     * Checks if this task falls on the same date as the given date.
     *
     * @return True if the task is on the same date; false otherwise.
     */
    public boolean doesTaskFallOnDate(LocalDate givenDate) {
        return this.dateTime.isSameDate(givenDate);
    }

    /**
     * Returns task description of this task.
     *
     * @return The task's description
     */
    public Description getDesc() {
        return desc;
    }

    /**
     * Returns the dateTime of this task.
     *
     * @return The task's dateTime
     */
    public DateTime getDateTime() {
        return dateTime;
    }

    /**
     * Returns the date of this task.
     *
     * @return The task's date
     */
    public LocalDate getDate() {
        return dateTime.date;
    }

    /**
     * Returns the time of this task.
     *
     * @return The task's time
     */
    public LocalTime getTime() {
        return dateTime.time;
    }

    /**
     * Returns the task's related immutable name set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Name> getRelatedNames() {
        return Collections.unmodifiableSet(relatedNames);
    }

    /**
     * Replaces the name {@code target} of the task with {@code editedName}.
     */
    public void replaceName(Name target, Name editedName) {
        deleteName(target);
        addName(editedName);
    }

    /**
     * Deletes the name {@code target} of the task.
     */
    public void deleteName(Name target) {
        relatedNames.remove(target);
    }

    /**
     * Adds the name {@code target} of the task.
     */
    public void addName(Name target) {
        relatedNames.add(target);
    }

    /**
     * Returns completion status of this task.
     *
     * @return The task's status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Returns the recurrence type of this task.
     *
     * @return The task's recurrence type
     */
    public Recurrence getRecurrence() {
        return recurrence;
    }

    /**
     * Changes the dateTime of the task to the intended dateTime
     *
     * @param dt The intended dateTime
     */
    public void setDateTime(DateTime dt) {
        this.dateTime = dt;
    }

    /**
     * Changes the date of the task to the intended date
     *
     * @param newDate The intended date
     */
    public void setDate(LocalDate newDate) {
        this.dateTime = new DateTime(newDate, this.dateTime.getTime());
    }

    /**
     * Copies the task and returns it.
     *
     * @return A copy of the current task.
     */
    public abstract Task copyTask();

    /**
     * Returns true if task dateTime is after the date given in the argument.
     *
     * @param date                      date to be compared to
     * @return                          true if is after, false otherwise
     */
    public boolean isAfter(DateTime date) {
        return dateTime.isAfter(date);
    }

    /**
     * Returns true if task dateTime is before the date given in the argument.
     *
     * @param date                      date to be compared to
     * @return                          true if is before, false otherwise
     */
    public boolean isBefore(DateTime date) {
        return dateTime.isBefore(date);
    }

    /**
     * Returns true if task has been marked as complete
     */
    public boolean isTaskDone() {
        return status.isDone;
    }

    /**
     * Returns true if task is overdue
     */
    public boolean isTaskOverdue() {
        return status.isOverdue;
    }

    /**
     * Returns true if task is recurring and overdue
     */
    public boolean isTaskRecurringAndOverdue() {
        return getRecurrence().isRecurring() && DateTime.isOverdue(getDateTime());
    }

    /**
     * Returns set of elderly objects related to this task.
     *
     * @param book                      nursey book that stores this task
     * @return                          task description
     */
    public Set<Elderly> getRelatedPeople(NurseyBook book) {
        Set<Elderly> relatedPeople = new HashSet<>();
        for (Name name: relatedNames) {
            relatedPeople.add(book.getElderly(name));
        }
        return relatedPeople;
    }

    /**
     * Returns true if the task is past the current date and time, and it is a recurring task.
     *
     * @return true if its past and is a recurring task
     */
    public boolean isPastCurrentDateAndRecurringTask() {
        return DateTime.isOverdue(this.dateTime) && recurrence.isRecurring();
    }

    /**
     * Returns true if both tasks have the same {@code Description} and {@code DateTime}.
     * This defines a weaker notion of equality between two tasks.
     */
    public boolean isSameTask(Task otherTask) {
        if (otherTask == this) {
            return true;
        }

        return otherTask != null
                && otherTask.getDesc().equals(getDesc()) && otherTask.getDateTime().equals(getDateTime())
                && otherTask.getRelatedNames().equals(getRelatedNames());
    }

    protected DateTime changeTaskDate(LocalDateTime currentDateTime, RecurrenceType recurrenceType) {
        LocalDate taskDate = getDateTime().date;
        LocalTime taskTime = getDateTime().time;

        LocalDateTime taskLocalDateTime = LocalDateTime.of(taskDate, taskTime);
        long daysBetween = Duration.between(taskLocalDateTime, currentDateTime).toDays();

        return changeTaskDateBasedOnRecurrence(recurrenceType, taskLocalDateTime, daysBetween);
    }

    private DateTime changeTaskDateBasedOnRecurrence(RecurrenceType recurrenceType,
                                                     LocalDateTime taskLocalDateTime, long daysBetween) {
        LocalDateTime taskNewLocalDateTime;

        if (recurrenceType == RecurrenceType.DAY) {
            taskNewLocalDateTime = taskLocalDateTime.plusDays(daysBetween + 1);

        } else if (recurrenceType == RecurrenceType.WEEK) {
            int daysToAdd = ((int) (daysBetween / 7)) * 7 + 7;
            taskNewLocalDateTime = taskLocalDateTime.plusDays(daysToAdd);

        } else {
            // assume its + 4 weeks
            int daysToAdd = ((int) (daysBetween / 28)) * 28 + 28;
            taskNewLocalDateTime = taskLocalDateTime.plusDays(daysToAdd);
        }
        // time is fixed
        return new DateTime(taskNewLocalDateTime.toLocalDate(), taskLocalDateTime.toLocalTime());
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getDesc())
                .append("; When: ")
                .append(getDateTime());
        if (!relatedNames.isEmpty()) {
            builder.append("; People: ");
            relatedNames.forEach(name -> {
                builder.append(name + " ");
            });
        }
        return builder.toString();
    }

    @Override
    public int compareTo(Task o) {
        return this.dateTime.compareTo(o.dateTime);
    }

    @Override
    public boolean equals(Object obj) {
        Task other = (Task) obj;

        return (other.desc).equals(desc)
                && (other.dateTime).equals(dateTime)
                && (other.relatedNames).equals(relatedNames)
                && (other.status).equals(status)
                && (other.recurrence).equals(recurrence);
    }
}