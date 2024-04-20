package com.example.quitburn.repository

import com.example.quitburn.dao.MoodDao
import com.example.quitburn.model.Mood
import kotlinx.coroutines.flow.Flow

class MoodRepository(private val moodDao: MoodDao) {

    suspend fun insertMood(mood: Mood) {
        moodDao.insert(mood)
    }

    fun getAllMood(): Flow<List<Mood>> {
        return moodDao.getAllMood()
    }
}