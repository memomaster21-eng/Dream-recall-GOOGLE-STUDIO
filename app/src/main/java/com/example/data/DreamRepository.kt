package com.example.data

import kotlinx.coroutines.flow.Flow

class DreamRepository(private val dreamDao: DreamDao) {
    val allDreams: Flow<List<Dream>> = dreamDao.getAllDreams()
    val totalCount: Flow<Int> = dreamDao.getTotalCount()
    val lucidCount: Flow<Int> = dreamDao.getLucidCount()

    fun getDream(id: Int): Flow<Dream?> = dreamDao.getDreamById(id)

    suspend fun insert(dream: Dream) = dreamDao.insertDream(dream)
    
    suspend fun update(dream: Dream) = dreamDao.updateDream(dream)
    
    suspend fun delete(dream: Dream) = dreamDao.deleteDream(dream)
}
