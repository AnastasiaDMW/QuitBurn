package com.example.quitburn.dao

import androidx.room.Insert

interface BaseDao<T> {
    @Insert
    suspend fun insert(vararg obj: T)
}