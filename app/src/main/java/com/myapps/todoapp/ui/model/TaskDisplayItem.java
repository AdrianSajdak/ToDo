package com.myapps.todoapp.ui.model;

import com.myapps.todoapp.data.model.Category;
import com.myapps.todoapp.data.model.Task;

public class TaskDisplayItem {

    public enum ItemType {
        CATEGORY,
        TASK
    }

    private final Object object;
    public final ItemType type;

    public final int level;
    public final boolean isExpanded;
    public final boolean hasSubTasks;

    public TaskDisplayItem(Category category, boolean isExpanded) {
        this.object = category;
        this.type = ItemType.CATEGORY;
        this.isExpanded = isExpanded;
        this.level = 0;
        this.hasSubTasks = true;
    }

    public TaskDisplayItem(Task task, int level, boolean isExpanded, boolean hasSubTasks) {
        this.object = task;
        this.type = ItemType.TASK;
        this.level = level;
        this.isExpanded = isExpanded;
        this.hasSubTasks = hasSubTasks;
    }

    public long getId() {
        if (type == ItemType.CATEGORY) {
            return -((Category) object).getId();
        } else {
            return ((Task) object).getId();
        }
    }

    public Object getObject() {
        return object;
    }
}