package com.example.quitburn.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.quitburn.model.Progress
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {

    @Update
    suspend fun updateProgress(progress: Progress)

    @Query("SELECT * FROM progress WHERE id=0")
    fun getProgress(): Flow<Progress>

    @Insert
    suspend fun insertProgress(progress: Progress)

}