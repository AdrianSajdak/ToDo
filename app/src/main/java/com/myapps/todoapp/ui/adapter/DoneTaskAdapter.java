package com.myapps.todoapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.myapps.todoapp.R;
import com.myapps.todoapp.data.model.Task;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class DoneTaskAdapter extends ListAdapter<Object, RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_TASK = 1;

    private final DoneTaskClickListener clickListener;

    public interface DoneTaskClickListener {
        void onTaskRestore(long taskId);
        void onTaskDelete(long taskId);
    }

    public DoneTaskAdapter(DoneTaskClickListener clickListener) {
        super(DIFF_CALLBACK);
        this.clickListener = clickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) instanceof String) {
            return VIEW_TYPE_HEADER;
        }
        return VIEW_TYPE_TASK;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_date_header, parent, false);
            return new HeaderViewHolder(view);
        }
        View view = inflater.inflate(R.layout.item_done_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            ((HeaderViewHolder) holder).bind((String) getItem(position));
        } else {
            ((TaskViewHolder) holder).bind((Task) getItem(position), clickListener);
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerTitle;
        HeaderViewHolder(View itemView) {
            super(itemView);
            headerTitle = itemView.findViewById(R.id.textView_header);
        }
        void bind(String title) {
            headerTitle.setText(title);
        }
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle;
        TextView completionDate;
        CheckBox taskCheckbox;
        MaterialButton moreOptionsButton;
        View priorityIndicator;

        TaskViewHolder(View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.textView_task_title);
            completionDate = itemView.findViewById(R.id.textView_completion_date);
            taskCheckbox = itemView.findViewById(R.id.checkBox_task_completed);
            moreOptionsButton = itemView.findViewById(R.id.more_options_button);
            priorityIndicator = itemView.findViewById(R.id.priority_indicator);
        }

        void bind(Task task, DoneTaskClickListener listener) {
            taskTitle.setText(task.getTitle());
            
            // Set priority color
            setPriorityColor(task.getPriority());
            
            // Format completion date
            if (task.getCompletionDate() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM HH:mm", Locale.getDefault());
                String formattedDate = "Wykonano: " + sdf.format(new Date(task.getCompletionDate()));
                completionDate.setText(formattedDate);
                completionDate.setVisibility(View.VISIBLE);
            } else {
                completionDate.setVisibility(View.GONE);
            }

            // Checkbox listener - restore task when unchecked
            taskCheckbox.setOnCheckedChangeListener(null); // Clear previous listener
            taskCheckbox.setChecked(true);
            taskCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!isChecked && listener != null) {
                    listener.onTaskRestore(task.getId());
                }
            });

            // More options button listener
            moreOptionsButton.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                popup.getMenuInflater().inflate(R.menu.done_task_menu, popup.getMenu());
                
                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.action_restore) {
                        if (listener != null) {
                            listener.onTaskRestore(task.getId());
                        }
                        return true;
                    } else if (item.getItemId() == R.id.action_delete) {
                        if (listener != null) {
                            listener.onTaskDelete(task.getId());
                        }
                        return true;
                    }
                    return false;
                });
                
                popup.show();
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

    private static final DiffUtil.ItemCallback<Object> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Object>() {
                @Override
                public boolean areItemsTheSame(@NonNull Object oldItem, @NonNull Object newItem) {
                    if (oldItem instanceof Task && newItem instanceof Task) {
                        return ((Task) oldItem).getId() == ((Task) newItem).getId();
                    }
                    if (oldItem instanceof String && newItem instanceof String) {
                        return oldItem.equals(newItem);
                    }
                    return false;
                }
                @Override
                public boolean areContentsTheSame(@NonNull Object oldItem, @NonNull Object newItem) {
                    if (oldItem instanceof Task && newItem instanceof Task) {
                        return Objects.equals(((Task) oldItem).getTitle(), ((Task) newItem).getTitle());
                    }
                    return areItemsTheSame(oldItem, newItem);
                }
            };
}