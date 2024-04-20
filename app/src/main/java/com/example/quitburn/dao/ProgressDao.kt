package com.example.quitburn.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.example.quitburn.model.Progress
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ProgressDao: BaseDao<Progress> {
    @Update
    abstract suspend fun updateProgress(progress: Progress)

    @Query("SELECT * FROM progress WHERE id=1")
    abstract fun getProgress(): Flow<Progress>
}