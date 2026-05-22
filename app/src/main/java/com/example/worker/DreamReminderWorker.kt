package com.example.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.MainActivity
import com.example.R
import com.example.data.DreamDatabase

class DreamReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val sharedPrefs = context.getSharedPreferences("dream_settings", Context.MODE_PRIVATE)
        val isEnabled = sharedPrefs.getBoolean("reminder_enabled", false)
        if (!isEnabled) {
            return Result.success()
        }

        val periodDays = sharedPrefs.getInt("reminder_period_days", 7)
        val timestampThreshold = System.currentTimeMillis() - (periodDays * 24L * 60L * 60L * 1000L)

        val database = DreamDatabase.getDatabase(context)
        val dream = database.dreamDao().getRandomDreamSince(timestampThreshold)

        if (dream != null) {
            val apiKey = sharedPrefs.getString("gemini_api_key", "") ?: ""
            if (apiKey.isNotBlank()) {
                val aiService = com.example.ai.AiService(apiKey)
                val aiNotificationText = aiService.generateShortNotification(dream.content)
                showNotification(dream.title.ifEmpty { "إضاءة من عقلك الباطن" }, aiNotificationText)
            }
        }

        return Result.success()
    }

    private fun showNotification(title: String, content: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        val channelId = "dream_reminder_channel"
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "تذكير الأحلام"
            val descriptionText = "إشعارات لتذكيرك بأحلامك السابقة"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("تذكر هذا الحلم؟ 💭")
            .setContentText(title)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }
}
