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
        PeriodicWorkRequest resetRequest =
                new PeriodicWorkRequest.Builder(RecurringTaskResetWorker.class, 1, TimeUnit.DAYS)
                        .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "RecurringTaskReset",
                ExistingPeriodicWorkPolicy.KEEP,
                resetRequest);
    }
}
