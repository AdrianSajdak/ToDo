package com.myapps.todoapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.myapps.todoapp.data.AppDatabase;
import com.myapps.todoapp.data.dao.CategoryDao;
import com.myapps.todoapp.data.dao.TaskDao;
import com.myapps.todoapp.data.model.Category;
import com.myapps.todoapp.data.model.Task;
import com.myapps.todoapp.ui.model.TaskDisplayItem;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainViewModel extends AndroidViewModel {

    private final CategoryDao categoryDao;
    private final TaskDao taskDao;
    private final LiveData<List<Category>> allCategories;
    private final Map<Long, Boolean> expansionState = new HashMap<>();
    private final MutableLiveData<Long> activeCategoryId = new MutableLiveData<>();
    public final LiveData<List<TaskDisplayItem>> tasksForActiveCategory;


    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getDatabase(application);
        categoryDao = database.categoryDao();
        taskDao = database.taskDao();
        allCategories = categoryDao.getAllCategories();
        tasksForActiveCategory = Transformations.switchMap(activeCategoryId, categoryId -> {
            return getTaskTreeForCategory(categoryId);
        });

    }

    public void toggleTaskExpansion(long taskId) {
        boolean isCurrentlyExpanded = expansionState.getOrDefault(taskId, true);
        expansionState.put(taskId, !isCurrentlyExpanded);
        activeCategoryId.setValue(activeCategoryId.getValue());
    }

    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }

    public LiveData<List<TaskDisplayItem>> getTaskTreeForCategory(long categoryId) {
        MutableLiveData<List<TaskDisplayItem>> liveData = new MutableLiveData<>();

        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<Task> allTasks = taskDao.getAllTasksForCategorySync(categoryId);
            Map<Long, List<Task>> tasksByParent = new HashMap<>();
            for (Task task : allTasks) {
                Long parentId = task.getParentTaskId();
                if (parentId == null) {
                    parentId = 0L;
                }
                if (!tasksByParent.containsKey(parentId)) {
                    tasksByParent.put(parentId, new ArrayList<>());
                }
                tasksByParent.get(parentId).add(task);
            }

            List<TaskDisplayItem> flattenedList = new ArrayList<>();
            buildFlattenedList(0L, 0, flattenedList, tasksByParent);

            liveData.postValue(flattenedList);
        });
        return liveData;
    }

    public void insert(Category category) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            categoryDao.insert(category);
        });
    }

    public void loadTasksForCategory(long categoryId) {
        if (activeCategoryId.getValue() == null || !activeCategoryId.getValue().equals(categoryId)) {
            activeCategoryId.setValue(categoryId);
        }
    }

    public void onTaskCheckedChanged(Task task, boolean isCompleted) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            task.setCompleted(isCompleted);
            task.setCompletionDate(isCompleted ? System.currentTimeMillis() : null);
            taskDao.update(task);
        });
    }

    private void buildFlattenedList(Long parentId, int depth, List<TaskDisplayItem> flattenedList, Map<Long, List<Task>> tasksByParent) {
        List<Task> children = tasksByParent.get(parentId);
        if (children == null) {
            return;
        }

        for (Task child : children) {
            if (child.isCompleted() && !child.isRecurring()) {
                continue;
            }

            boolean hasChildren = tasksByParent.containsKey(child.getId());
            boolean allChildrenCompleted = false;

            if (hasChildren) {
                List<Task> grandChildren = tasksByParent.get(child.getId());
                if (grandChildren != null && !grandChildren.isEmpty()) {
                    allChildrenCompleted = grandChildren.stream().allMatch(Task::isCompleted);
                }
            }

            boolean isExpanded = expansionState.getOrDefault(child.getId(), true);
            flattenedList.add(new TaskDisplayItem(child, depth, hasChildren, isExpanded, allChildrenCompleted));

            if (hasChildren && isExpanded) {
                buildFlattenedList(child.getId(), depth + 1, flattenedList, tasksByParent);
            }
        }
    }
}