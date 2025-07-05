package com.myapps.todoapp.data.worker;

import android.app.Application;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.myapps.todoapp.data.TaskRepository;
import com.myapps.todoapp.data.model.Task;
import java.util.Calendar;
import java.util.List;

public class RecurringTaskResetWorker extends Worker {

    private TaskRepository repository;

    public RecurringTaskResetWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        repository = new TaskRepository((Application) context.getApplicationContext());
    }

    @NonNull
    @Override
    public Result doWork() {
        List<Task> recurringTasks = repository.getRecurringTasksSync();
        long today = getStartOfDay(System.currentTimeMillis());

        for (Task task : recurringTasks) {
            Long lastCompletion = task.getLastRecurrenceCompletionDate();
            if (lastCompletion == null || getStartOfDay(lastCompletion) < today) {
                // Sprawdź, czy reguła cykliczności pozwala na odświeżenie dzisiaj
                if (shouldResetToday(task)) {
                    task.setCompleted(false); // "Odśwież" zadanie
                    repository.updateTask(task);
                }
            }
        }
        return Result.success();
    }

    private boolean shouldResetToday(Task task) {
        String rule = task.getRecurrenceRule();
        if (rule == null) return false;
        
        Calendar today = Calendar.getInstance();
        Long lastCompletion = task.getLastRecurrenceCompletionDate();
        
        switch (rule) {
            case "DAILY":
                return true; // Codziennie
            case "WEEKLY":
                // Co tydzień od dnia utworzenia
                if (lastCompletion == null) return true;
                Calendar lastCompletionCal = Calendar.getInstance();
                lastCompletionCal.setTimeInMillis(lastCompletion);
                return today.get(Calendar.WEEK_OF_YEAR) > lastCompletionCal.get(Calendar.WEEK_OF_YEAR) ||
                       today.get(Calendar.YEAR) > lastCompletionCal.get(Calendar.YEAR);
            case "MONTHLY":
                // Co miesiąc
                if (lastCompletion == null) return true;
                Calendar lastCompletionCalMonth = Calendar.getInstance();
                lastCompletionCalMonth.setTimeInMillis(lastCompletion);
                return today.get(Calendar.MONTH) > lastCompletionCalMonth.get(Calendar.MONTH) ||
                       today.get(Calendar.YEAR) > lastCompletionCalMonth.get(Calendar.YEAR);
            default:
                return false;
        }
    }

    private long getStartOfDay(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
}