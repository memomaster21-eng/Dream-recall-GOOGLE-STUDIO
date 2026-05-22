package com.example.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.ai.AiService
import com.example.data.DreamDatabase
import kotlinx.coroutines.flow.firstOrNull

class AiSyncWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val sharedPrefs = context.getSharedPreferences("dream_settings", Context.MODE_PRIVATE)
        val apiKey = sharedPrefs.getString("gemini_api_key", "") ?: ""
        if (apiKey.isBlank()) return Result.success()

        val database = DreamDatabase.getDatabase(context)
        val dao = database.dreamDao()
        val allDreams = dao.getAllDreams().firstOrNull() ?: emptyList()

        val aiService = AiService(apiKey)

        for (dream in allDreams) {
            if (dream.aiAnalysis.isNullOrBlank()) {
                val analysis = aiService.analyzeDream(dream.title, dream.content, dream.isLucid, dream.isRecurring)
                if (!analysis.startsWith("حدث خطأ")) {
                    dao.updateDream(dream.copy(aiAnalysis = analysis))
                }
            }
        }

        return Result.success()
    }
}
