package com.example.quitburn.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.quitburn.dao.MoodDao
import com.example.quitburn.dao.ProgressDao
import com.example.quitburn.model.Mood
import com.example.quitburn.model.Progress
import kotlinx.coroutines.CoroutineScope

@Database(entities = [Mood::class, Progress::class], version = 1, exportSchema = false)
abstract class QuitBurnDatabase: RoomDatabase() {

    abstract fun moodDao(): MoodDao
    abstract fun progressDao(): ProgressDao

    companion object {

        @Volatile
        private var INSTANCE: QuitBurnDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): QuitBurnDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, QuitBurnDatabase::class.java, "progress_database")
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}