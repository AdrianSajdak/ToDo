package com.myapps.todoapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import com.myapps.todoapp.data.TaskRepository;
import com.myapps.todoapp.data.model.Category;
import com.myapps.todoapp.data.model.Task;
import com.myapps.todoapp.ui.model.TaskDisplayItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainViewModel extends AndroidViewModel {

    private final TaskRepository repository;
    private final ExecutorService executorService;
    private final Set<Long> expandedCategories = new HashSet<>();
    private final Map<Long, Boolean> expandedTasks = new HashMap<>();
    private final MediatorLiveData<List<TaskDisplayItem>> displayList = new MediatorLiveData<>();
    private final LiveData<List<Category>> allCategories;
    private final Map<Long, LiveData<List<Task>>> tasksPerCategory = new HashMap<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = new TaskRepository(application);
        executorService = Executors.newSingleThreadExecutor();
        allCategories = repository.getAllCategories();
        displayList.addSource(allCategories, categories -> rebuildDisplayList());
    }

    public LiveData<List<TaskDisplayItem>> getDisplayList() {
        return displayList;
    }

    public void insertCategory(Category category) {
        repository.insertCategory(category);
    }

    public void insertTask(Task task) {
        repository.insertTask(task);
    }

    public void updateTaskCompletion(long taskId, boolean isCompleted) {
        repository.updateTaskCompletion(taskId, isCompleted);
    }

    public void toggleCategoryExpansion(long categoryId) {
        if (expandedCategories.contains(categoryId)) {
            expandedCategories.remove(categoryId);
            if (tasksPerCategory.containsKey(categoryId)) {
                displayList.removeSource(tasksPerCategory.get(categoryId));
                tasksPerCategory.remove(categoryId);
            }
        } else {
            expandedCategories.add(categoryId);
            LiveData<List<Task>> tasksSource = repository.getTopLevelTasksForCategory(categoryId);
            tasksPerCategory.put(categoryId, tasksSource);
            displayList.addSource(tasksSource, tasks -> rebuildDisplayList());
        }
        rebuildDisplayList();
    }

    private void rebuildDisplayList() {
        executorService.execute(() -> {
            List<Category> categories = allCategories.getValue();
            if (categories == null) return;

            List<TaskDisplayItem> newDisplayList = new ArrayList<>();
            for (Category category : categories) {
                newDisplayList.add(new TaskDisplayItem(category, expandedCategories.contains(category.getId())));
                if (expandedCategories.contains(category.getId())) {
                    LiveData<List<Task>> tasksLiveData = tasksPerCategory.get(category.getId());
                    if (tasksLiveData != null && tasksLiveData.getValue() != null) {
                        addTasksToList(newDisplayList, tasksLiveData.getValue(), 1);
                    }
                }
            }
            displayList.postValue(newDisplayList);
        });
    }

    private void addTasksToList(List<TaskDisplayItem> list, List<Task> tasks, int level) {
        if (tasks == null) return;
        for (Task task : tasks) {
            boolean isExpanded = expandedTasks.getOrDefault(task.getId(), false);
            List<Task> subTasks = repository.getSubTasks(task.getId());
            boolean hasSubTasks = !subTasks.isEmpty();
            list.add(new TaskDisplayItem(task, level, isExpanded, hasSubTasks));
            if (hasSubTasks && isExpanded) {
                addTasksToList(list, subTasks, level + 1);
            }
        }
    }

    public void toggleTaskExpansion(long taskId) {
        boolean isCurrentlyExpanded = expandedTasks.getOrDefault(taskId, false);
        expandedTasks.put(taskId, !isCurrentlyExpanded);
        rebuildDisplayList();
    }

    public boolean isCategoryExpanded(long categoryId) {
        return expandedCategories.contains(categoryId);
    }

    public boolean isTaskExpanded(long taskId) {
        return expandedTasks.getOrDefault(taskId, false);
    }
}