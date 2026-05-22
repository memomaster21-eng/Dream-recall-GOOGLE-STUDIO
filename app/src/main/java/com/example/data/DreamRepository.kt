package com.example.data

import kotlinx.coroutines.flow.Flow

class DreamRepository(private val dao: DreamDao) {
    val allDreams: Flow<List<Dream>> = dao.getAllDreams()

    fun getDreamById(id: Int): Flow<Dream?> = dao.getDreamById(id)

    suspend fun insertDream(dream: Dream) {
        dao.insertDream(dream)
    }

    suspend fun updateDream(dream: Dream) {
        dao.updateDream(dream)
    }

    suspend fun deleteDream(dream: Dream) {
        dao.deleteDream(dream)
    }
}
