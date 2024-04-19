package com.example.quitburn.repository

import com.example.quitburn.dao.MoodDao
import com.example.quitburn.model.Mood
import kotlinx.coroutines.flow.Flow

class OfflineMoodRepository(private val moodDao: MoodDao): MoodRepository {

    override suspend fun insertMood(mood: Mood) {
        moodDao.insertMood(mood)
    }

    override fun getAllMood(): Flow<List<Mood>> {
        return moodDao.getAllMood()
    }
}