package com.example.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object ReminderHelper {
    private const val WORK_NAME = "DreamReminderWork"

    fun scheduleReminders(context: Context, frequencyHours: Long) {
        val workRequest = PeriodicWorkRequestBuilder<DreamReminderWorker>(frequencyHours, TimeUnit.HOURS)
            .build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    fun cancelReminders(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}
