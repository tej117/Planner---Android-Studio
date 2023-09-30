package tej.android.calendarproject;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class TaskData {

    //Data object attributes
    private int id;
    private String taskName;
    private String notes;
    private LocalDateTime taskTime;
    private LocalDate taskDate;
    //private LocalDateTime reminderDate;
    private boolean isTimeSet;
    private boolean isReminder;

    //Constructor
    public TaskData(int id, String taskName, String notes, LocalDateTime taskTime, LocalDate taskDate,
                    boolean isTimeSet, boolean isReminder) {
        this.id = id;
        this.taskName = taskName;
        this.notes = notes;
        this.taskTime = taskTime;
        this.taskDate = taskDate;
        this.isTimeSet = isTimeSet;
        this.isReminder = isReminder;
    }

    //Getter Methods
    public int getId() {return id;}
    public String getTaskName() {return taskName;}
    public String getNotes() {return notes;}
    public LocalDate getTaskDate() {return taskDate;}
    public LocalDateTime getTaskTime() {return taskTime;}
    public boolean getIsTimeSet() {return isTimeSet;}
    public boolean getIsReminder() {return isReminder;}

    @Override
    public String toString() {
        return "TaskData{" +
                "id=" + id +
                ", taskName='" + taskName + '\'' +
                ", notes='" + notes + '\'' +
                ", taskDate=" + taskDate.toString() +
                ", isTimeSet=" + isTimeSet +
                ", isReminder=" + isReminder +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskData td = (TaskData) o;
        return (this.id == td.getId() && this.taskName.equals(td.getTaskName()) &&
                this.notes.equals(td.getNotes()) && this.taskDate.equals(td.getTaskDate()) &&
                this.isTimeSet == td.getIsTimeSet() && this.isReminder == td.getIsReminder());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskName, notes, taskDate, isTimeSet, isReminder);
    }

    //Used for recycler view list adapter - Compares a data object with items in list
    public static DiffUtil.ItemCallback<TaskData> itemCallback = new DiffUtil.ItemCallback<TaskData>() {
        @Override
        public boolean areItemsTheSame(@NonNull TaskData oldItem, @NonNull TaskData newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull TaskData oldItem, @NonNull TaskData newItem) {
            return oldItem.equals(newItem);
        }
    };
}
