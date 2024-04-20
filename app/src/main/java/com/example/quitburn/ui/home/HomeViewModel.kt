package com.example.quitburn.ui.home

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.quitburn.Constant.CHANNEL_ID
import com.example.quitburn.Constant.PREFERENCES_KEY_COUNTER
import com.example.quitburn.Constant.PREFERENCES_KEY_FACT_INDEX
import com.example.quitburn.Constant.PREFERENCES_KEY_MOOD_TODAY
import com.example.quitburn.Constant.PREFERENCES_KEY_SMOKER
import com.example.quitburn.Constant.UNIQUE_WORK_NAME
import com.example.quitburn.Constant.motivationList
import com.example.quitburn.QuitBurnApplication
import com.example.quitburn.data.HourlyWorker
import com.example.quitburn.model.Mood
import com.example.quitburn.model.Progress
import com.example.quitburn.repository.OfflineMoodRepository
import com.example.quitburn.repository.OfflineProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class HomeViewModel(
    private val repositoryMood: OfflineMoodRepository,
    private val repositoryProgress: OfflineProgressRepository
): ViewModel() {

    companion object {
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
        val COUNTER = intPreferencesKey(PREFERENCES_KEY_COUNTER)
        val SMOKER = booleanPreferencesKey(PREFERENCES_KEY_SMOKER)
        val FACT_INDEX = intPreferencesKey(PREFERENCES_KEY_FACT_INDEX)
        val MOOD_TODAY = booleanPreferencesKey(PREFERENCES_KEY_MOOD_TODAY)
    }

    var isLoading: Boolean = true

    val allProgress: LiveData<Progress> = repositoryProgress.getProgress().asLiveData()

    fun insertMood(mood: Mood) = viewModelScope.launch {
        repositoryMood.insertMood(mood)
    }

    fun insertProgress(progress: Progress) = viewModelScope.launch {
        repositoryProgress.insertProgress(progress)
    }

    fun updateProgress(progress: Progress) = viewModelScope.launch {
        repositoryProgress.updateProgress(progress)
    }

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "channel"
            val descriptionText = "mood channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun startWorkManager(context: Context) {
        val workManager = WorkManager.getInstance(context)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresCharging(false)
            .setRequiresBatteryNotLow(false)
            .setRequiresDeviceIdle(false)
            .setRequiresStorageNotLow(false)
            .build()

        val periodicWorkRequestBuilder =
            PeriodicWorkRequestBuilder<HourlyWorker>(10, TimeUnit.MINUTES)

        val periodicWorkRequest = periodicWorkRequestBuilder.setConstraints(constraints).build()
        workManager.enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }

    fun getDayString(number: Int): String {
        if (number % 10 == 1 && number % 100 != 11) {
            return "день"
        } else if ((number % 10 == 2 || number % 10 == 3 || number % 10 == 4) && (number % 100 < 10 || number % 100 >= 20)) {
            return "дня"
        } else {
            return "дней"
        }
    }

    suspend fun selectRandomFact(context: Context) {
        context.dataStore.edit { settings ->
            settings[FACT_INDEX] = Random.nextInt(motivationList.size-1)
        }
    }

    fun readFactIndex(context: Context): Flow<Int> {
        return context.dataStore.data
            .map { preferences ->
                preferences[FACT_INDEX] ?: 0
            }
    }

    fun stopWorkManager(context: Context) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(UNIQUE_WORK_NAME)
        workManager.cancelAllWorkByTag(UNIQUE_WORK_NAME)
    }

    fun readCounter(context: Context): Flow<Int> {
        return context.dataStore.data
            .map { preferences ->
                preferences[COUNTER] ?: 0
            }
    }

    suspend fun incrementCounter(context: Context) {
        context.dataStore.edit { settings ->
            val currentCounterValue = settings[COUNTER] ?: 0
            settings[COUNTER] = currentCounterValue + 1
        }
    }

    suspend fun clearCounter(context: Context) {
        context.dataStore.edit { settings ->
            settings[COUNTER] = 0
        }
    }

    fun readIsCheckMoodToday(context: Context): Flow<Boolean> {
        return context.dataStore.data
            .map {preferences ->
                preferences[MOOD_TODAY] ?: true
            }
    }

    suspend fun changeValueIsCheckMoodToday(context: Context, value: Boolean) {
        context.dataStore.edit { settings ->
//            val currentValue = settings[MOOD_TODAY] ?: false
            settings[MOOD_TODAY] = value
        }
    }

    fun readIsSmoker(context: Context): Flow<Boolean> {
        return context.dataStore.data
            .map { preferences ->
                preferences[SMOKER] ?: true
            }
    }

    suspend fun changeValueIsSmoker(context: Context) {
        context.dataStore.edit { settings ->
            val currentValue = settings[SMOKER] ?: true
            settings[SMOKER] = !currentValue
        }
    }
}

class HomeViewModelProvider(
    private val repositoryMood: OfflineMoodRepository,
    private val repositoryProgress: OfflineProgressRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)){
            return HomeViewModel(repositoryMood, repositoryProgress) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}