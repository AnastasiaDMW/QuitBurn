package com.example.quitburn.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.quitburn.model.Mood
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodDao {

    @Insert
    suspend fun insertMood(mood: Mood)

    @Query("SELECT * FROM mood")
    fun getAllMood(): Flow<List<Mood>>

}