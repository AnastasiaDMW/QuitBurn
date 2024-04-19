package com.example.quitburn.repository

import com.example.quitburn.dao.ProgressDao
import com.example.quitburn.model.Progress
import kotlinx.coroutines.flow.Flow

class OfflineProgressRepository(private val progressDao: ProgressDao): ProgressRepository {

    override suspend fun updateProgress(progress: Progress) {
        progressDao.updateProgress(progress)
    }

    override fun getProgress(): Flow<Progress> {
        return progressDao.getProgress()
    }

    override suspend fun insertProgress(progress: Progress) {
        progressDao.insertProgress(progress)
    }
}