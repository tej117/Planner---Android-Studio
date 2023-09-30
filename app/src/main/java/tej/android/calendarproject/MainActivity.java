package tej.android.calendarproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageButton;

import java.time.LocalDate;

public class MainActivity extends AppCompatActivity {

    //Class Attributes
    private CalendarView calendarView;
    private ImageButton deleteDB;
    private LocalDate date;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        //Initialize the database
        dbHelper = DBHelper.getInstance(MainActivity.this);

        //Setting up the calendar
        calendarView = findViewById(R.id.cvCalendar);
        calendarView.setMinDate(System.currentTimeMillis() - 1000); //Grays out calendar dates

        //Delete old tasks
        LocalDate d = LocalDate.now();
        dbHelper.deleteOldTasks(d);

        //Set up the delete button
        deleteDB = findViewById(R.id.imbtnDeleteDB);
        deleteDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearDatabase(DBHelper.TASK_DATA_TABLE);
            }
        });

        //Go to new task list activity
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                date = LocalDate.of(year, month+1, day);    //Put calendar date clicked into variable

                //Transfer date to new activity
                Intent goToTaskList = new Intent(MainActivity.this, TaskListActivity.class);
                goToTaskList.putExtra("date", date);
                startActivity(goToTaskList);
            }
        });
    }

    //delete all the rows in the table
    public void clearDatabase(String TABLE_NAME) {
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        String clearDBQuery = "DELETE FROM "+TABLE_NAME;
//        db.execSQL(clearDBQuery);
//        db.close();
        this.deleteDatabase(TABLE_NAME);
    }
}