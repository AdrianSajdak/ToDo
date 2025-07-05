package com.myapps.todoapp.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks",
        indices = {@Index(value = "categoryId")},
        foreignKeys = @ForeignKey(entity = Category.class,
                parentColumns = "id",
                childColumns = "categoryId",
                onDelete = ForeignKey.CASCADE))
public class Task {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long categoryId;

    private Long parentTaskId;
    private String title;
    private String description;
    private boolean isCompleted;
    private int priority;
    private Long deadline;
    private long creationDate;
    private Long completionDate;
    private boolean isRecurring;
    private String recurrenceRule;
    private Long lastRecurrenceCompletionDate;

    public Task() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getCategoryId() { return categoryId; }
    public void setCategoryId(long categoryId) { this.categoryId = categoryId; }
    public Long getParentTaskId() { return parentTaskId; }
    public void setParentTaskId(Long parentTaskId) { this.parentTaskId = parentTaskId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public Long getDeadline() { return deadline; }
    public void setDeadline(Long deadline) { this.deadline = deadline; }
    public long getCreationDate() { return creationDate; }
    public void setCreationDate(long creationDate) { this.creationDate = creationDate; }
    public Long getCompletionDate() { return completionDate; }
    public void setCompletionDate(Long completionDate) { this.completionDate = completionDate; }
    public boolean isRecurring() { return isRecurring; }
    public void setRecurring(boolean recurring) { isRecurring = recurring; }
    public String getRecurrenceRule() { return recurrenceRule; }
    public void setRecurrenceRule(String recurrenceRule) { this.recurrenceRule = recurrenceRule; }
    public Long getLastRecurrenceCompletionDate() { return lastRecurrenceCompletionDate; }
    public void setLastRecurrenceCompletionDate(Long lastRecurrenceCompletionDate) { this.lastRecurrenceCompletionDate = lastRecurrenceCompletionDate; }
}