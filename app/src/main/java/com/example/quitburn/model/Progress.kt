package com.example.quitburn.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "progress")
data class Progress(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo("start_date")
    val startDate: String,
    @ColumnInfo("count_stop")
    val countStop: Int
)
