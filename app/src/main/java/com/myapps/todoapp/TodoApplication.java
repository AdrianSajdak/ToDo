package com.myapps.todoapp;

import android.app.Application;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.myapps.todoapp.data.worker.RecurringTaskResetWorker;
import java.util.concurrent.TimeUnit;

public class TodoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        scheduleRecurringTaskReset();
    }

    private void scheduleRecurringTaskReset() {
        // Uruchamiaj zadanie tylko gdy urządzenie jest bezczynne
        Constraints constraints = new Constraints.Builder()
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(true)
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build();

        // Uruchamiaj zadanie raz dziennie
        PeriodicWorkRequest resetRequest =
                new PeriodicWorkRequest.Builder(RecurringTaskResetWorker.class, 1, TimeUnit.DAYS)
                        .setConstraints(constraints)
                        .build();

        // Użyj unikalnej nazwy, aby uniknąć wielokrotnego planowania tego samego zadania
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "recurring_task_reset_work",
                ExistingPeriodicWorkPolicy.KEEP, // Zachowaj istniejące zadanie, jeśli już jest zaplanowane
                resetRequest
        );
    }
}
