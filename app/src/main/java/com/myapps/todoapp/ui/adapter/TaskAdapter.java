package com.myapps.todoapp.ui.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageSwitcher;
import android.widget.Space;
import android.widget.TextView;
import android.graphics.Paint;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myapps.todoapp.R;
import com.myapps.todoapp.ui.model.TaskDisplayItem;
import com.myapps.todoapp.data.model.Task;
import com.myapps.todoapp.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;



public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<TaskDisplayItem> taskItems = new ArrayList<>();
    private final MainViewModel viewModel;

    public TaskAdapter(MainViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskDisplayItem currentItem = taskItems.get(position);
        Task task = currentItem.task;
        holder.textViewTaskTitle.setText(task.getTitle());

        // --- Logika wcięć (bez zmian) ---
        int indentationDp = 24;
        int indentationPx = (int) dpToPx(holder.itemView.getContext(), indentationDp * currentItem.depth);
        ViewGroup.LayoutParams params = holder.indentationSpace.getLayoutParams();
        params.width = indentationPx;
        holder.indentationSpace.setLayoutParams(params);

        // --- NOWA, PEŁNA LOGIKA INTERFEJSU ---

        // 1. Ustaw styl (przekreślenie) dla ukończonych zadań
        if (task.isCompleted()) {
            holder.textViewTaskTitle.setPaintFlags(holder.textViewTaskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.textViewTaskTitle.setAlpha(0.5f);
        } else {
            holder.textViewTaskTitle.setPaintFlags(holder.textViewTaskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.textViewTaskTitle.setAlpha(1.0f);
        }

        // 2. Obsługa zadań nadrzędnych
        if (currentItem.hasChildren) {
            holder.expandIcon.setVisibility(View.VISIBLE);
            holder.expandIcon.setImageResource(
                    currentItem.isExpanded ? R.drawable.ic_arrow_down : R.drawable.ic_arrow_right
            );
            holder.itemView.setOnClickListener(v -> viewModel.toggleTaskExpansion(task.getId()));

            // Checkbox dla rodzica jest widoczny, ale klikalny tylko gdy wszystkie dzieci są ukończone
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setEnabled(currentItem.allChildrenCompleted);

        } else {
            // 3. Obsługa zadań końcowych (bez dzieci)
            holder.expandIcon.setVisibility(View.GONE);
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setEnabled(true); // Zawsze klikalny
            holder.itemView.setOnClickListener(null);
        }

        // 4. Ustaw stan checkboxa i listener
        // Najpierw usuwamy listener, aby uniknąć wywołania przy programowej zmianie stanu
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(task.isCompleted());

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Wywołaj metodę w ViewModelu tylko jeśli stan faktycznie się zmienił
            if (task.isCompleted() != isChecked) {
                viewModel.onTaskCheckedChanged(task, isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskItems.size();
    }

    public void setTaskItems(List<TaskDisplayItem> taskItems) {
        this.taskItems = taskItems;
        notifyDataSetChanged();
    }

    private float dpToPx(Context context, int dp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()
        );
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTaskTitle;
        private final Space indentationSpace;
        private final CheckBox checkBox;
        public ImageSwitcher expandIcon;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTaskTitle = itemView.findViewById(R.id.text_view_task_title);
            indentationSpace = itemView.findViewById(R.id.indentation_space);
            checkBox = itemView.findViewById(R.id.checkbox_task_completed);
        }
    }
}