package tej.android.calendarproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class TaskDetailsDialogFragment extends DialogFragment {

    //Dialog fragment attributes
    private EditText taskNameDialog;
    private EditText descriptionDialog;
    private Switch setTimeDialog;
    private Switch setReminderDialog;
    private Button doneDialog;
    private Button cancelDialog;

    LinearLayout timePickerViewDialog;
    TimePicker timePickerDialog;

    //Define the listener interface with a method passing back data result
    public interface TaskDetailsDialogListener {
        void taskDataDialog(String inputTask, String inputDesc, boolean isTimeTrue, boolean isRemindTrue,
                            int timeHour, int timeMinute);
    }

    //Empty constructor
    public TaskDetailsDialogFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Set up the dialog fragment views
        View view = inflater.inflate(R.layout.fragment_task_details, container, false);

        taskNameDialog = view.findViewById(R.id.etTaskName);
        descriptionDialog = view.findViewById(R.id.etDescription);
        setTimeDialog = view.findViewById(R.id.swSetTime);
        setReminderDialog = view.findViewById(R.id.swSetReminder);
        doneDialog = view.findViewById(R.id.btTaskDone);
        cancelDialog = view.findViewById(R.id.btTaskCancel);

        timePickerDialog = view.findViewById(R.id.tmPickerSpinner);
        timePickerViewDialog = view.findViewById(R.id.vwTimePicker);
        timePickerViewDialog.setVisibility(View.INVISIBLE);
        timePickerViewDialog.getLayoutParams().height = 0;
        timePickerDialog.requestLayout();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        //If timepicker switch is clicked, show timepicker view
        setTimeDialog.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton cb, boolean isChecked) {
                if(isChecked) {
                    timePickerViewDialog.setVisibility(View.VISIBLE);
                    timePickerViewDialog.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    timePickerDialog.requestLayout();
                    TranslateAnimation animate = new TranslateAnimation(0, 0,
                            timePickerViewDialog.getHeight(), 0);
                    animate.setDuration(500);
                    animate.setFillAfter(true);
                    timePickerViewDialog.startAnimation(animate);
                } else {
                    timePickerViewDialog.setVisibility(View.INVISIBLE);
                    timePickerViewDialog.getLayoutParams().height = 0;
                    timePickerDialog.requestLayout();
                    TranslateAnimation animate = new TranslateAnimation(0, 0, 0,
                            timePickerViewDialog.getHeight());
                    animate.setDuration(500);
                    animate.setFillAfter(true);
                    timePickerViewDialog.startAnimation(animate);
                }
            }
        });

        //When the done button is clicked, take the properties from the edit texts and send back to prev activity
        doneDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = 0, minutes = 0;
                TaskDetailsDialogListener td = (TaskDetailsDialogListener) getActivity();

                if (taskNameDialog.getText().toString().equals("")) {
                    Toast.makeText(getActivity(),"Please type a task name",Toast.LENGTH_SHORT).show();
                } else {
                    if(setTimeDialog.isChecked()) {
                        hour = timePickerDialog.getHour();
                        minutes = timePickerDialog.getMinute();
                    }

                    td.taskDataDialog(taskNameDialog.getText().toString(), descriptionDialog.getText().toString(),
                            setTimeDialog.isChecked(), setReminderDialog.isChecked(), hour, minutes);
                    dismiss();
                }
            }
        });

        //When the cancel button is clicked, dismiss dialog
        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

}
