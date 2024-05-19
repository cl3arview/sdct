package com.mobiledev.sdct.adapters;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobiledev.sdct.R;
import com.mobiledev.sdct.models.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private OnTaskInteractionListener listener;
    private boolean viewingCompletedTasks;

    public TaskAdapter(List<Task> taskList, OnTaskInteractionListener listener, boolean viewingCompletedTasks) {
        this.taskList = taskList;
        this.listener = listener;
        this.viewingCompletedTasks = viewingCompletedTasks;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.textViewTask.setText(task.getText());

        if (viewingCompletedTasks) {
            holder.checkBoxCompleted.setVisibility(View.GONE);
            holder.buttonUndo.setVisibility(View.VISIBLE);
        } else {
            holder.checkBoxCompleted.setVisibility(View.VISIBLE);
            holder.checkBoxCompleted.setChecked(task.isCompleted());
            holder.checkBoxCompleted.setOnClickListener(v -> listener.onCompleteTask(task));
            holder.buttonUndo.setVisibility(View.GONE);
        }

        holder.buttonDelete.setOnClickListener(v -> listener.onDeleteTask(task));
        holder.buttonUndo.setOnClickListener(v -> listener.onUndoTask(task));

        if (task.isCompleted()) {
            holder.textViewTask.setPaintFlags(holder.textViewTask.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.textViewTask.setPaintFlags(holder.textViewTask.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTask;
        CheckBox checkBoxCompleted;
        Button buttonDelete;
        Button buttonUndo;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTask = itemView.findViewById(R.id.textViewTask);
            checkBoxCompleted = itemView.findViewById(R.id.checkBoxCompleted);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonUndo = itemView.findViewById(R.id.buttonUndo);
        }
    }

    public interface OnTaskInteractionListener {
        void onCompleteTask(Task task);
        void onDeleteTask(Task task);
        void onUndoTask(Task task);
    }
}
