package com.example.quitburn.repository

import com.example.quitburn.model.Mood
import kotlinx.coroutines.flow.Flow

interface MoodRepository {

    suspend fun insertMood(mood: Mood)

    fun getAllMood(): Flow<List<Mood>>

}