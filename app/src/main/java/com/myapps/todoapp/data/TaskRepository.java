package com.myapps.todoapp.data;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.myapps.todoapp.data.dao.CategoryDao;
import com.myapps.todoapp.data.dao.TaskDao;
import com.myapps.todoapp.data.model.Category;
import com.myapps.todoapp.data.model.Task;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskRepository {
    private final CategoryDao categoryDao;
    private final TaskDao taskDao;
    private final LiveData<List<Category>> allCategories;
    private final ExecutorService executorService;

    public TaskRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        categoryDao = db.categoryDao();
        taskDao = db.taskDao();
        allCategories = categoryDao.getAllCategories();
        executorService = Executors.newSingleThreadExecutor();
    }

    // --- Metody dla Kategorii ---
    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    public void insertCategory(Category category) {
        validateCategory(category);
        executorService.execute(() -> categoryDao.insert(category));
    }

    // --- Metody dla Zadań ---
    public LiveData<List<Task>> getTopLevelTasksForCategory(long categoryId) {
        return taskDao.getTopLevelTasksForCategory(categoryId);
    }

    // Zwraca zwykłą listę - do synchronicznego użycia w ViewModelu
    public List<Task> getSubTasks(long taskId) {
        return taskDao.getSubTasks(taskId);
    }

    // Zwraca LiveData - do asynchronicznej obserwacji (jeśli potrzebne)
    public LiveData<List<Task>> getSubTasksLiveData(long taskId) {
        return taskDao.getSubTasksLiveData(taskId);
    }

    public LiveData<List<Task>> getCompletedTasks() {
        return taskDao.getCompletedTasks();
    }

    public List<Task> getRecurringTasksSync() {
        return taskDao.getRecurringTasks();
    }

    public void insertTask(Task task) {
        validateTask(task);
        task.setCreationDate(System.currentTimeMillis());
        executorService.execute(() -> taskDao.insert(task));
    }

    public void updateTask(Task task) {
        executorService.execute(() -> taskDao.update(task));
    }

    public void updateTaskCompletion(long taskId, boolean isCompleted) {
        Long completionDate = isCompleted ? System.currentTimeMillis() : null;
        executorService.execute(() -> taskDao.updateTaskCompletion(taskId, isCompleted, completionDate));
    }

    public void deleteTask(Task task) {
        executorService.execute(() -> taskDao.delete(task));
    }

    public void restoreTask(long taskId) {
        updateTaskCompletion(taskId, false);
    }

    public void deleteTaskById(long taskId) {
        executorService.execute(() -> taskDao.deleteById(taskId));
    }
    
    /**
     * Validates task data before insertion/update
     * @param task Task to validate
     * @throws IllegalArgumentException if task data is invalid
     */
    private void validateTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }
        
        if (task.getTitle().length() < 3) {
            throw new IllegalArgumentException("Task title must be at least 3 characters long");
        }
        
        if (task.getTitle().length() > 100) {
            throw new IllegalArgumentException("Task title cannot be longer than 100 characters");
        }
        
        if (task.getPriority() < 0 || task.getPriority() > 2) {
            throw new IllegalArgumentException("Task priority must be between 0 and 2");
        }
        
        if (task.getCategoryId() <= 0) {
            throw new IllegalArgumentException("Task must have a valid category ID");
        }
    }
    
    /**
     * Validates category data before insertion/update
     * @param category Category to validate
     * @throws IllegalArgumentException if category data is invalid
     */
    private void validateCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }
        
        if (category.getName().length() < 2) {
            throw new IllegalArgumentException("Category name must be at least 2 characters long");
        }
        
        if (category.getName().length() > 50) {
            throw new IllegalArgumentException("Category name cannot be longer than 50 characters");
        }
    }
}
