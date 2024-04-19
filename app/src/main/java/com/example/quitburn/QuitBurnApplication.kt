package com.example.quitburn

import android.app.Application
import com.example.quitburn.database.QuitBurnDatabase
import com.example.quitburn.repository.OfflineMoodRepository
import com.example.quitburn.repository.OfflineProgressRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class QuitBurnApplication: Application() {

    private val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { QuitBurnDatabase.getDatabase(this, applicationScope) }
    val moodRepository by lazy { OfflineMoodRepository(database.moodDao()) }
    val progressRepository by lazy { OfflineProgressRepository(database.progressDao()) }

}