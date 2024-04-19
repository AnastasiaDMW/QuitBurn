package com.example.quitburn.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mood")
data class Mood(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val date: String,
    @ColumnInfo("mood_name")
    val moodName: String
)
