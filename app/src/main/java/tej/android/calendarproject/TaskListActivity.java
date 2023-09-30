package tej.android.calendarproject;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TaskListActivity extends AppCompatActivity implements TaskDetailsDialogFragment.TaskDetailsDialogListener, TaskListAdapter.TaskClickInterface {

    //Attributes of the class
    private LocalDate currentDate;
    private TextView currentDateTextView;
    private FloatingActionButton addTaskButton;

    //Recycler View Stuff
    private RecyclerView taskList;
    private TaskListAdapter taskListAdapter;
    private RecyclerView.LayoutManager taskListLayoutManager;

    //DataBase stuff
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        //Set the views
        currentDateTextView = findViewById(R.id.tvCurrentDateTitle);
        addTaskButton = findViewById(R.id.fabAddTask);

        //Set stuff for Recycler View
        taskList = findViewById(R.id.rvTaskList);
        taskList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        //Set Linear Layout Manager
        taskListLayoutManager = new LinearLayoutManager(this);
        taskList.setLayoutManager(taskListLayoutManager);
        //set the adapter
        taskListAdapter = new TaskListAdapter(TaskData.itemCallback, this);
        taskList.setAdapter(taskListAdapter);

        //Get the date from the calendar
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            currentDate = (LocalDate) extras.get("date");

        }

        //Set the date for the text view
        currentDateTextView.setText(currentDate.format(DateTimeFormatter.ISO_LOCAL_DATE));

        //Setup the database
        dbHelper = DBHelper.getInstance(TaskListActivity.this);
        getTasksDB();

        //Set onClickListener for the addTaskButton
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create a dialog popup
                showTaskDialog();
            }
        });

    }

    //Get tasks from the database and add the list to the recycler view
    private void getTasksDB() {
        List<TaskData> l = new ArrayList<>(dbHelper.getAllTasksInDate(currentDate));
        taskListAdapter.submitList(l);
    }

    //Runs the task dialog fragment
    private void showTaskDialog() {
        TaskDetailsDialogFragment taskDetailsDialogFragment = new TaskDetailsDialogFragment();
        taskDetailsDialogFragment.show(getSupportFragmentManager(), "fragment_task_details");
    }

    //Creates the data object
    private TaskData createDataObject(String inputTask, String inputDesc, LocalDateTime taskTime, boolean isTimeTrue, boolean isRemindTrue) {
        return new TaskData(-1, inputTask, inputDesc, taskTime, currentDate, isTimeTrue, isRemindTrue);
    }

    //Interface methods - TaskDetailsDialogListener - Used to grab dialog information
    @Override
    public void taskDataDialog(String inputTask, String inputDesc, boolean isTimeTrue, boolean isRemindTrue, int timeHour, int timeMinute) {

        //Create dataTime object from current date and time received from dialog fragment
        LocalDateTime dt = LocalDateTime.of(currentDate.getYear(), currentDate.getMonth(), currentDate.getDayOfMonth(),
                timeHour, timeMinute);
        TaskData td = createDataObject(inputTask, inputDesc, dt, isTimeTrue, isRemindTrue); //Create data object

        //add data object to database
        dbHelper.addTask(td);

        //add to recycler view adapter
        List<TaskData> l = new ArrayList<>(taskListAdapter.getCurrentList());
        l.add(td);
        taskListAdapter.submitList(l);
    }

    @Override
    public void onDelete(int position) {
        //delete from database
        dbHelper.deleteTask(taskListAdapter.getCurrentList().get(position));

        //delete from recycler view adapter
        List<TaskData> l = new ArrayList<>(taskListAdapter.getCurrentList());
        l.remove(position);
        taskListAdapter.submitList(l);
    }
}