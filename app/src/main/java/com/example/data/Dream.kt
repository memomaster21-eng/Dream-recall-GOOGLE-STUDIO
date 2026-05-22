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
    val tags: String = "",
    val sleepDuration: Float = 8f
)
