package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dreams")
data class Dream(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
    val mood: String = "Neutral",
    val vividness: Float = 5f,
    val isLucid: Boolean = false,
    val isNightmare: Boolean = false,
    val isRecurring: Boolean = false,
    val isFavorite: Boolean = false,
    val dreamType: String = "عادي",
    val tags: String = "",
    val sleepDuration: Float = 8f,
    val aiAnalysis: String? = null
)

@Entity(tableName = "sleep_sessions")
data class SleepSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startTime: Long,
    val endTime: Long? = null,
    val fazzaTimes: String = "[]", // JSON list of Long timestamps
    val isCompleted: Boolean = false
)
