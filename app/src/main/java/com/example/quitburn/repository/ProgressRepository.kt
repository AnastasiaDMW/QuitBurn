package com.example.quitburn.repository

import com.example.quitburn.dao.ProgressDao
import com.example.quitburn.model.Progress
import kotlinx.coroutines.flow.Flow

class ProgressRepository(private val progressDao: ProgressDao) {

    suspend fun updateProgress(progress: Progress) {
        progressDao.updateProgress(progress)
    }

    fun getProgress(): Flow<Progress> {
        return progressDao.getProgress()
    }

    suspend fun insertProgress(progress: Progress) {
        progressDao.insert(progress)
    }
}