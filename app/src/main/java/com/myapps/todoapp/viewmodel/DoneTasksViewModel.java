package com.myapps.todoapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.myapps.todoapp.data.AppDatabase;
import com.myapps.todoapp.data.dao.TaskDao;
import com.myapps.todoapp.data.model.Task;

import java.util.List;

public class DoneTasksViewModel extends AndroidViewModel {
    private final TaskDao taskDao;
    private final LiveData<List<Task>> completedTasks;

    public DoneTasksViewModel(@NonNull Application application) {
        super(application);
        taskDao = AppDatabase.getDatabase(application).taskDao();
        completedTasks = taskDao.getCompletedNonRecurringTasks();
    }

    public LiveData<List<Task>> getCompletedTasks() {
        return completedTasks;
    }
}