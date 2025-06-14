package com.myapps.todoapp.data.worker;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.myapps.todoapp.data.AppDatabase;
import com.myapps.todoapp.data.dao.TaskDao;
import com.myapps.todoapp.data.model.Task;
import java.util.Calendar;
import java.util.List;

public class RecurringTaskResetWorker extends Worker {

    private static final String TAG = "RecurringTaskReset";

    public RecurringTaskResetWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Rozpoczynanie pracy resetowania zadań cyklicznych.");
        TaskDao taskDao = AppDatabase.getDatabase(getApplicationContext()).taskDao();
        List<Task> recurringTasks = taskDao.getAllRecurringTasksSync();

        Calendar today = Calendar.getInstance();
        // Ustaw godzinę na początek dnia, aby uniknąć problemów ze strefami czasowymi
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        for (Task task : recurringTasks) {
            if (task.isCompleted()) {
                Calendar completionDate = Calendar.getInstance();
                completionDate.setTimeInMillis(task.getCompletionDate());
                completionDate.set(Calendar.HOUR_OF_DAY, 0);
                // Prosta logika dla zadań codziennych
                if ("DAILY".equals(task.getRecurrenceRule()) && completionDate.before(today)) {
                    task.setCompleted(false);
                    taskDao.update(task);
                    Log.d(TAG, "Zresetowano zadanie: " + task.getTitle());
                }
                // Tutaj można dodać bardziej złożoną logikę dla "WEEKLY", "MONTHLY" itp.
            }
        }
        Log.d(TAG, "Zakończono pracę resetowania zadań cyklicznych.");
        return Result.success();
    }
}