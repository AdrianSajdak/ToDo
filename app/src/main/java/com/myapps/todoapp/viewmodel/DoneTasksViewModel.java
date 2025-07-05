package com.myapps.todoapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.myapps.todoapp.data.TaskRepository;
import com.myapps.todoapp.data.model.Task;
import java.util.List;

public class DoneTasksViewModel extends AndroidViewModel {

    private final TaskRepository repository;
    private final LiveData<List<Task>> completedTasks;

    public DoneTasksViewModel(@NonNull Application application) {
        super(application);
        repository = new TaskRepository(application);
        completedTasks = repository.getCompletedTasks();
    }

    public LiveData<List<Task>> getCompletedTasks() {
        return completedTasks;
    }

    public void restoreTask(long taskId) {
        repository.restoreTask(taskId);
    }

    public void deleteTask(long taskId) {
        repository.deleteTaskById(taskId);
    }
}