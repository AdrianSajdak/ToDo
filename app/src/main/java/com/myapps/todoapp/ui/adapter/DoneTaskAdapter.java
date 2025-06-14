package com.myapps.todoapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myapps.todoapp.R;
import com.myapps.todoapp.data.model.Task;

import java.util.ArrayList;
import java.util.List;

public class DoneTaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_TASK = 1;
    private List<Object> items = new ArrayList<>();

    // ViewHolder dla nagłówka
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerTitle;
        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            headerTitle = itemView.findViewById(R.id.text_view_header);
        }
    }

    // ViewHolder dla zadania
    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle;
        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.text_view_task_title);
        }
    }

    public void submitList(List<Object> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof String) {
            return TYPE_HEADER;
        }
        return TYPE_TASK;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date_header, parent, false);
            return new HeaderViewHolder(view);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_done_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_HEADER) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.headerTitle.setText((String) items.get(position));
        } else {
            TaskViewHolder taskHolder = (TaskViewHolder) holder;
            Task task = (Task) items.get(position);
            taskHolder.taskTitle.setText(task.getTitle());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}