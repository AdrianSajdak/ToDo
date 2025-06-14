package com.myapps.todoapp.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks",
    foreignKeys = {
        @ForeignKey(entity = Category.class,
            parentColumns = "id",
            childColumns = "categoryId",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(entity = Task.class,
            parentColumns = "id",
            childColumns = "parentTaskId",
            onDelete = ForeignKey.CASCADE
        )
    })
public class Task {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(index = true)
    private long categoryId;

    @ColumnInfo(index = true)
    private Long parentTaskId;

    private String title;
    private String description;
    private boolean isCompleted;
    private int priority; // np. 0-Niski, 1-Średni, 2-Wysoki
    private Long deadline; //Timestamp
    private long creationDate; //Timestamp
    private Long completionDate; //Timestamp
    private boolean isRecurring;
    private String recurrenceRule;
    private Long lastRecurrenceCompletionDate;

    public Task(){}

    // GETTERS AND SETTERS
    public long getCategoryId(){return categoryId;}
    public void setCategoryId(long categoryId){this.categoryId = categoryId;}

    public Long getParentTaskId(){return parentTaskId;}
    public void setParentTaskId(Long parentTaskId){this.parentTaskId = parentTaskId;}

    public long getId(){return id;}
    public void setId(long id){this.id = id;}

    public String getTitle(){return title;}
    public void setTitle(String title){this.title = title;}

    public String getDescription(){return description;}
    public void setDescription(String description){this.description = description;}

    public boolean isCompleted(){return isCompleted;}
    public void setCompleted(boolean isCompleted){this.isCompleted = isCompleted;}

    public int getPriority(){return priority;}
    public void setPriority(int priority){this.priority = priority;}

    public Long getDeadline(){return deadline;}
    public void setDeadline(Long deadline){this.deadline = deadline;}

    public long getCreationDate(){return creationDate;}
    public void setCreationDate(long creationDate){this.creationDate = creationDate;}

    public Long getCompletionDate(){return completionDate;}
    public void setCompletionDate(Long completionDate){this.completionDate = completionDate;}

    public boolean isRecurring(){return isRecurring;}
    public void setRecurring(boolean isRecurring){this.isRecurring = isRecurring;}

    public String getRecurrenceRule(){return recurrenceRule;}
    public void setRecurrenceRule(String recurrenceRule){this.recurrenceRule = recurrenceRule;}

    public Long getLastRecurrenceCompletionDate(){return lastRecurrenceCompletionDate;}
    public void setLastRecurrenceCompletionDate(Long lastRecurrenceCompletionDate){this.lastRecurrenceCompletionDate = lastRecurrenceCompletionDate;}
}
