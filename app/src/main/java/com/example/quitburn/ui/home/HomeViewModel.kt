package com.example.quitburn.ui.home

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.quitburn.Constant.PREFERENCES_KEY_COUNTER
import com.example.quitburn.Constant.PREFERENCES_KEY_FACT_INDEX
import com.example.quitburn.Constant.PREFERENCES_KEY_MOOD_TODAY
import com.example.quitburn.Constant.PREFERENCES_KEY_SMOKER
import com.example.quitburn.Constant.UNIQUE_WORK_NAME
import com.example.quitburn.data.DataStoreManager
import com.example.quitburn.data.HourlyWorker
import com.example.quitburn.model.Mood
import com.example.quitburn.model.Progress
import com.example.quitburn.repository.MoodRepository
import com.example.quitburn.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class HomeViewModel(
    private val repositoryMood: MoodRepository,
    private val repositoryProgress: ProgressRepository
): ViewModel() {
    private lateinit var workManager: WorkManager
    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
        .build()

    fun setWorkManager(workManager: WorkManager) {
        this.workManager = workManager
    }
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun startWorkManager() {
        val workRequest = PeriodicWorkRequestBuilder<HourlyWorker>(
            repeatInterval = 15,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest)
    }

    fun stopWorkManager() {
        workManager.cancelUniqueWork(UNIQUE_WORK_NAME)
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

    fun readFactIndex(context: Context): Flow<Int> {
        return context.dataStore.data
            .map { preferences ->
                preferences[FACT_INDEX] ?: 0
            }
    }

    fun readCounter(context: Context): Flow<Int> {
        val dataStoreManager = DataStoreManager(context)
        return dataStoreManager.readCounter()
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
        val dataStoreManager = DataStoreManager(context)
        dataStoreManager.changeValueIsCheckMoodToday(value)
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
    private val repositoryMood: MoodRepository,
    private val repositoryProgress: ProgressRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)){
            return HomeViewModel(repositoryMood, repositoryProgress) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}