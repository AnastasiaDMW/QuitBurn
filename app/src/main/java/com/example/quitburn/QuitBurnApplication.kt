package com.example.quitburn

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.quitburn.database.QuitBurnDatabase
import com.example.quitburn.repository.MoodRepository
import com.example.quitburn.repository.ProgressRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class QuitBurnApplication: Application() {

    private val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { QuitBurnDatabase.getDatabase(this, applicationScope) }
    val moodRepository by lazy { MoodRepository(database.moodDao()) }
    val progressRepository by lazy { ProgressRepository(database.progressDao()) }

}