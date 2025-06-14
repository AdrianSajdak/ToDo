package com.myapps.todoapp.ui.model;

import com.myapps.todoapp.data.model.Task;

public class TaskDisplayItem {
    public Task task;
    public int depth;
    public boolean hasChildren;
    public boolean isExpanded;
    public boolean allChildrenCompleted;

    public TaskDisplayItem(Task task, int depth, boolean hasChildren, boolean isExpanded, boolean allChildrenCompleted) {
        this.task = task;
        this.depth = depth;
        this.hasChildren = hasChildren;
        this.isExpanded = isExpanded;
        this.allChildrenCompleted = allChildrenCompleted;
    }
}
