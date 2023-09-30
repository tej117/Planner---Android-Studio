package tej.android.calendarproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TaskListAdapter extends ListAdapter<TaskData, TaskListAdapter.ViewHolder> {

    //Attributes
    TaskClickInterface taskClickInterface;
    private ArrayList<TaskData> taskDataset;

    //Interface from onDelete method (for deleting a task from recycler view)
    interface TaskClickInterface {
        void onDelete(int position);
    }

    //Viewholder class AKA the view that is shown in the list
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTask;
        private final TextView tvDesc;
        private final TextView tvTime;
        private final ImageButton ibtnDelete;
        private ConstraintLayout clTaskCard;

        //Constructor
        public ViewHolder(@NonNull View view) {
            super(view);

            tvTask = view.findViewById(R.id.tvTask);
            tvDesc = view.findViewById(R.id.tvDescription);
            tvTime = view.findViewById(R.id.tvTime);
            ibtnDelete = view.findViewById(R.id.ibtnDeleteTask);
            clTaskCard = view.findViewById(R.id.clTaskCard);

            //If delete is pressed, than call taskClickInterface (which is set up in task list activity)
            ibtnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Deletes the task
                    taskClickInterface.onDelete(getAdapterPosition());
                }
            });
        }

        //Put all the information in the object within the view layout
        public void bind(TaskData td) {
            tvTask.setText(td.getTaskName());

            //Shrink the textview if there is no description
            String desc = td.getNotes();
            if(desc.equals("")) {
                clTaskCard.removeView(tvDesc);
            } else {
                tvDesc.setText(td.getNotes());
            }

            //Shrink the textview if there is no time
            if (!td.getIsTimeSet()) {
                clTaskCard.removeView(tvTime);
            } else {
                //Create Date String
                DateTimeFormatter f = DateTimeFormatter.ofPattern("hh:mm a");
                tvTime.setText(td.getTaskTime().format(f));
            }
        }
    }

    //The constructor
    protected TaskListAdapter(@NonNull DiffUtil.ItemCallback<TaskData> diffCallback, TaskClickInterface taskClickInterface) {
        super(diffCallback);
        this.taskClickInterface = taskClickInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_task_card, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaskData td = getItem(position);
        holder.bind(td);
    }
}
