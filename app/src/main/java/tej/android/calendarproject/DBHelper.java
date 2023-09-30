package tej.android.calendarproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class DBHelper extends SQLiteOpenHelper {

    //Class attributes
    private static DBHelper sInstance;

    public static final String TASK_DATA_TABLE = "TASK_DATA_TABLE";
    public static final String COLUMN_TASK_NAME = "TASK_NAME";
    public static final String COLUMN_TASK_NOTES = "TASK_NOTES";
    public static final String COLUMN_TASK_DATE = "TASK_DATE";
    public static final String COLUMN_TASK_TIME = "TASK_TIME";
    //public static final String COLUMN_TASK_REMINDER = "TASK_REMINDER";
    public static final String COLUMN_TASK_TIME_SET = "TASK_TIME_SET";
    //public static final String COLUMN_TASK_REMINDER_SET = "TASK_REMINDER_SET";
    public static final String COLUMN_ID = "ID";

    //Only creates one instance of the database when starting the app
    public static synchronized DBHelper getInstance(Context context) {
        //Use application context so that you don't accidentally leak an Activity's context
        if (sInstance == null) {
            sInstance = new DBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    //Constructor
    private DBHelper(@Nullable Context context) {
        super(context, "calendar.db", null, 1);
    }

    //this is called the first time a database is accessed - Creates the table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + TASK_DATA_TABLE + " " +
                "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_TASK_NAME + " TEXT, " +
                COLUMN_TASK_NOTES + " TEXT, " + COLUMN_TASK_DATE + " INTEGER, " + COLUMN_TASK_TIME + " INTEGER, " +
                COLUMN_TASK_TIME_SET + " BOOL)";

        db.execSQL(createTableStatement);
    }

    //this is called if the database version number changes, i.e. if you update the database
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    //Add a task to the database
    public boolean addTask(TaskData taskData) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues(); //Like a hashmap

        //Store the information into the sqlite database
        cv.put(COLUMN_TASK_NAME, taskData.getTaskName());
        cv.put(COLUMN_TASK_NOTES, taskData.getNotes());

        //Use the epoch system to store the dates
        ZoneId z = TimeZone.getDefault().toZoneId();
        cv.put(COLUMN_TASK_DATE, taskData.getTaskDate().atStartOfDay(z).toEpochSecond());
        ZonedDateTime zd = taskData.getTaskTime().atZone(TimeZone.getDefault().toZoneId());
        cv.put(COLUMN_TASK_TIME, zd.toEpochSecond());

        cv.put(COLUMN_TASK_TIME_SET, taskData.getIsTimeSet());

        long insert = db.insert(TASK_DATA_TABLE, null, cv);

        if (insert == -1) return false;
        else return true;
    }

    //Return a list of tasks based on the date given
    public List<TaskData> getAllTasksInDate(LocalDate calDate) {

        List<TaskData> returnList = new ArrayList<>();

        ZoneId z = TimeZone.getDefault().toZoneId();
        long epoch = calDate.atStartOfDay(z).toEpochSecond();

        String queryString = "SELECT * FROM " + TASK_DATA_TABLE + " WHERE " + COLUMN_TASK_DATE + "=" + epoch;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            //loop through the cursor (result set)
            do {
                int taskID = cursor.getInt(0);
                String taskName = cursor.getString(1);
                String taskNotes = cursor.getString(2);
                long taskDate = cursor.getLong(3);
                long taskTime = cursor.getLong(4);
                boolean taskTimeIsSet = cursor.getInt(5) == 1 ? true: false;

                LocalDate d = Instant.ofEpochMilli(taskDate).atZone(TimeZone.getDefault().toZoneId()).toLocalDate();
                LocalDateTime dt = Instant.ofEpochMilli(taskTime).atZone(TimeZone.getDefault().toZoneId()).toLocalDateTime();

                TaskData newTaskData = new TaskData(taskID, taskName, taskNotes, dt, d, taskTimeIsSet, false);
                returnList.add(newTaskData);
            } while (cursor.moveToNext());
        }

        //close both the cursor and db when done
        cursor.close();
        db.close();
        return returnList;
    }

    //Delete all tasks that are less than the given date
    public void deleteOldTasks(LocalDate currentDate) {
        ZoneId z = TimeZone.getDefault().toZoneId();
        long epoch = currentDate.atStartOfDay(z).toEpochSecond();

        String queryString = "SELECT * FROM " + TASK_DATA_TABLE + " WHERE " + COLUMN_TASK_DATE + "<" + epoch;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) {
            //loop through the cursor (result set)
            do {
                int taskID = cursor.getInt(0);
                String taskName = cursor.getString(1);
                String taskNotes = cursor.getString(2);
                long taskDate = cursor.getLong(3);
                long taskTime = cursor.getLong(4);
                boolean taskTimeIsSet = cursor.getInt(5) == 1 ? true: false;

                LocalDate d = Instant.ofEpochMilli(taskDate).atZone(TimeZone.getDefault().toZoneId()).toLocalDate();
                LocalDateTime dt = Instant.ofEpochMilli(taskTime).atZone(TimeZone.getDefault().toZoneId()).toLocalDateTime();

                TaskData newTaskData = new TaskData(taskID, taskName, taskNotes, dt, d, taskTimeIsSet, false);
                deleteTask(newTaskData);
            } while (cursor.moveToNext());
        }

        //close both the cursor and db when done
        cursor.close();
        db.close();
    }

    //Delete the task that is provided from the database
    public boolean deleteTask(TaskData taskData) {
        //find taskData in db and if found, delete it
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM " + TASK_DATA_TABLE + " WHERE " + COLUMN_ID + " == " +
                taskData.getId();
        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor.moveToFirst()) {
            return true;
        } else return false;
    }
}
