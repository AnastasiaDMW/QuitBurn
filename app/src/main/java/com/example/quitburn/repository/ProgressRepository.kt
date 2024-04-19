package com.example.quitburn.repository

import com.example.quitburn.model.Progress
import kotlinx.coroutines.flow.Flow

interface ProgressRepository {

    suspend fun updateProgress(progress: Progress)

    fun getProgress(): Flow<Progress>

    suspend fun insertProgress(progress: Progress)

}