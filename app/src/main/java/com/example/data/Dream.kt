package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dreams")
data class Dream(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
    val mood: String, // e.g. "Happy", "Anxious", "Neutral", "Weird", "Peaceful"
    val vividness: Int, // 1 to 10
    val isLucid: Boolean = false,
    val isNightmare: Boolean = false,
    val tags: String, // Comma separated for simplicity since Room needs TypeConverters for lists usually
    val sleepDurationMinutes: Int = 0
)
