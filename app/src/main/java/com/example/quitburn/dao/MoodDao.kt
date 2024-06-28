package com.example.quitburn.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.quitburn.model.Mood
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MoodDao: BaseDao<Mood> {
    @Query("SELECT * FROM mood")
    abstract fun getAllMood(): Flow<List<Mood>>

    @Query("DELETE FROM mood")
    abstract suspend fun deleteAllMood()
}