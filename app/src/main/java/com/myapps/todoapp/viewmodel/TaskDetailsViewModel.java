package com.myapps.todoapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.myapps.todoapp.data.AppDatabase;
import com.myapps.todoapp.data.dao.CategoryDao;
import com.myapps.todoapp.data.dao.TaskDao;
import com.myapps.todoapp.data.model.Category;
import com.myapps.todoapp.data.model.Task;

import java.util.List;

public class TaskDetailsViewModel extends AndroidViewModel {

    private final CategoryDao categoryDao;
    private final TaskDao taskDao;
    private final LiveData<List<Category>> allCategories;

    public TaskDetailsViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getDatabase(application);
        categoryDao = database.categoryDao();
        taskDao = database.taskDao();
        allCategories = categoryDao.getAllCategories();
    }

    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    public void insert(Task task) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            taskDao.insert(task);
        });
    }
}