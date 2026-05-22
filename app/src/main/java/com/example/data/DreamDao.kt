package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DreamDao {
    @Query("SELECT * FROM dreams ORDER BY createdAt DESC")
    fun getAllDreams(): Flow<List<Dream>>

    @Query("SELECT * FROM dreams WHERE id = :id")
    fun getDreamById(id: Int): Flow<Dream?>

    @Query("SELECT * FROM dreams WHERE createdAt >= :timestamp ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomDreamSince(timestamp: Long): Dream?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDream(dream: Dream)

    @Update
    suspend fun updateDream(dream: Dream)

    @Delete
    suspend fun deleteDream(dream: Dream)

    @Query("SELECT * FROM sleep_sessions ORDER BY startTime DESC")
    fun getAllSleepSessions(): Flow<List<SleepSession>>

    @Query("SELECT * FROM sleep_sessions WHERE isCompleted = 0 ORDER BY startTime DESC LIMIT 1")
    suspend fun getActiveSleepSession(): SleepSession?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleepSession(session: SleepSession)

    @Update
    suspend fun updateSleepSession(session: SleepSession)
}
