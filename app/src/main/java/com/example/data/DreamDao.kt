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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDream(dream: Dream)

    @Update
    suspend fun updateDream(dream: Dream)

    @Delete
    suspend fun deleteDream(dream: Dream)
}
