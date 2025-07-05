package com.myapps.todoapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.myapps.todoapp.R;
import com.myapps.todoapp.data.model.Category;
import com.myapps.todoapp.data.model.Task;
import com.myapps.todoapp.ui.model.TaskDisplayItem;

public class TaskListAdapter extends ListAdapter<TaskDisplayItem, RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_CATEGORY = 1;
    private static final int VIEW_TYPE_TASK = 2;

    private final TaskClickListener clickListener;

    public interface TaskClickListener {
        void onCategoryClick(long categoryId);
        void onTaskClick(long taskId);
        void onTaskCompleted(long taskId, boolean isCompleted);
    }

    public TaskListAdapter(TaskClickListener clickListener) {
        super(DIFF_CALLBACK);
        this.clickListener = clickListener;
    }

    @Override
    public int getItemViewType(int position) {
        TaskDisplayItem item = getItem(position);
        if (item.type == TaskDisplayItem.ItemType.CATEGORY) {
            return VIEW_TYPE_CATEGORY;
        }
        return VIEW_TYPE_TASK;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_CATEGORY) {
            View view = inflater.inflate(R.layout.item_category, parent, false);
            return new CategoryViewHolder(view);
        } else { // viewType == VIEW_TYPE_TASK
            View view = inflater.inflate(R.layout.item_task, parent, false);
            return new TaskViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TaskDisplayItem item = getItem(position);
        if (getItemViewType(position) == VIEW_TYPE_CATEGORY) {
            ((CategoryViewHolder) holder).bind((Category) item.getObject(), item.isExpanded, clickListener);
        } else {
            ((TaskViewHolder) holder).bind((Task) item.getObject(), item, clickListener);
        }
    }

    // ViewHolder dla Kategorii
    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        ImageView expandIcon;

        CategoryViewHolder(View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.textView_category_name);
            expandIcon = itemView.findViewById(R.id.imageView_expand_icon);
        }

        void bind(final Category category, boolean isExpanded, final TaskClickListener listener) {
            categoryName.setText(category.getName());
            expandIcon.setImageResource(isExpanded ? R.drawable.ic_arrow_down : R.drawable.ic_arrow_right);
            itemView.setOnClickListener(v -> listener.onCategoryClick(category.getId()));
        }
    }

    // ViewHolder dla Zadania
    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle;
        CheckBox taskCheckbox;
        ImageView expandIcon;
        View indentView; // Dodatkowy widok do wcięć
        View priorityIndicator; // Wskaźnik priorytetu

        TaskViewHolder(View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.textView_task_title);
            taskCheckbox = itemView.findViewById(R.id.checkBox_task_completed);
            expandIcon = itemView.findViewById(R.id.imageView_task_expand_icon);
            indentView = itemView.findViewById(R.id.indent_view);
            priorityIndicator = itemView.findViewById(R.id.priority_indicator);
        }

        void bind(final Task task, final TaskDisplayItem displayItem, final TaskClickListener listener) {
            taskTitle.setText(task.getTitle());
            taskCheckbox.setChecked(task.isCompleted());

            // Ustawienie koloru priorytetu
            setPriorityColor(task.getPriority());

            // Logika wcięć
            ViewGroup.LayoutParams params = indentView.getLayoutParams();
            int indentWidth = (int) (displayItem.level * 24 * itemView.getContext().getResources().getDisplayMetrics().density); // 24dp na poziom
            params.width = indentWidth;
            indentView.setLayoutParams(params);

            // Logika ikony rozwijania dla zadań
            if (displayItem.hasSubTasks) {
                expandIcon.setVisibility(View.VISIBLE);
                expandIcon.setImageResource(displayItem.isExpanded ? R.drawable.ic_arrow_down : R.drawable.ic_arrow_right);
            } else {
                expandIcon.setVisibility(View.GONE);
            }

            // Ustawienie listenera kliknięć
            itemView.setOnClickListener(v -> {
                if (displayItem.hasSubTasks) {
                    listener.onTaskClick(task.getId());
                }
            });

            // Obsługa checkboxa
            taskCheckbox.setOnCheckedChangeListener(null); // Clear previous listener
            taskCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onTaskCompleted(task.getId(), isChecked);
                }
            });
        }

        private void setPriorityColor(int priority) {
            int colorRes;
            switch (priority) {
                case 0: // Niski priorytet
                    colorRes = R.color.priority_low;
                    break;
                case 1: // Średni priorytet
                    colorRes = R.color.priority_medium;
                    break;
                case 2: // Wysoki priorytet
                    colorRes = R.color.priority_high;
                    break;
                default:
                    colorRes = R.color.priority_medium;
                    break;
            }
            int color = ContextCompat.getColor(itemView.getContext(), colorRes);
            priorityIndicator.setBackgroundColor(color);
        }
    }

    private static final DiffUtil.ItemCallback<TaskDisplayItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<TaskDisplayItem>() {
                @Override
                public boolean areItemsTheSame(@NonNull TaskDisplayItem oldItem, @NonNull TaskDisplayItem newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull TaskDisplayItem oldItem, @NonNull TaskDisplayItem newItem) {
                    // To jest uproszczenie. W pełni poprawna implementacja powinna porównywać
                    // zawartość obiektów (np. przez metodę equals()).
                    if(oldItem.type != newItem.type) return false;
                    return oldItem.isExpanded == newItem.isExpanded && oldItem.level == newItem.level;
                }
            };
}