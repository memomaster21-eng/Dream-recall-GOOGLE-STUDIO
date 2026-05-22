package com.example.data

import kotlinx.coroutines.flow.Flow

class DreamRepository(private val dao: DreamDao) {
    val allDreams: Flow<List<Dream>> = dao.getAllDreams()

    fun getDreamById(id: Int): Flow<Dream?> = dao.getDreamById(id)

    suspend fun getRandomDreamSince(timestamp: Long): Dream? = dao.getRandomDreamSince(timestamp)

    suspend fun insertDream(dream: Dream) {
        dao.insertDream(dream)
    }

    suspend fun updateDream(dream: Dream) {
        dao.updateDream(dream)
    }

    suspend fun deleteDream(dream: Dream) {
        dao.deleteDream(dream)
    }

    val allSleepSessions: Flow<List<SleepSession>> = dao.getAllSleepSessions()

    suspend fun getActiveSleepSession(): SleepSession? = dao.getActiveSleepSession()

    suspend fun insertSleepSession(session: SleepSession) {
        dao.insertSleepSession(session)
    }

    suspend fun updateSleepSession(session: SleepSession) {
        dao.updateSleepSession(session)
    }
}
