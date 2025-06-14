package com.myapps.todoapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.myapps.todoapp.data.model.Task;

import java.util.List;

@Dao
public interface TaskDao {
    @Insert
    void insert(Task task);
    @Update
    void update(Task task);
    @Delete
    void delete(Task task);

    // get tasks per category
    @Query("SELECT * FROM tasks WHERE categoryId = :categoryId AND parentTaskId IS NULL ORDER BY priority DESC, deadline ASC")
    LiveData<List<Task>> getTopLevelTasksForCategory(long categoryId);

    // get subTasks per main task
    @Query("SELECT * FROM tasks WHERE parentTaskId = :parentTaskId ORDER BY creationDate ASC")
    LiveData<List<Task>> getSubTasksForTask(long parentTaskId);

    // get completed tasks
    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY completionDate DESC")
    LiveData<List<Task>> getCompletedTasks();

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    LiveData<List<Task>> getTaskById(long taskId);

    @Query("SELECT * FROM tasks WHERE categoryId = :categoryId ORDER BY creationDate ASC")
    List<Task> getAllTasksForCategorySync(long categoryId);

    @Query("SELECT * FROM tasks WHERE isCompleted = 1 AND isRecurring = 0 ORDER BY completionDate DESC")
    LiveData<List<Task>> getCompletedNonRecurringTasks();

    @Query("SELECT * FROM tasks WHERE isRecurring = 1")
    List<Task> getAllRecurringTasksSync();
}
