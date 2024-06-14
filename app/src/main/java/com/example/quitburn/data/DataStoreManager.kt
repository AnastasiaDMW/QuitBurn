package com.example.quitburn.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.example.quitburn.Constant
import com.example.quitburn.ui.home.HomeViewModel
import com.example.quitburn.ui.home.HomeViewModel.Companion.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.random.Random

class DataStoreManager(private val context: Context) {

    suspend fun incrementDays() {
        context.dataStore.edit { settings ->
            val currentCounterValue = settings[HomeViewModel.COUNTER] ?: 0
            settings[HomeViewModel.COUNTER] = currentCounterValue + 1
        }
    }

    suspend fun changeValueIsCheckMoodToday(value: Boolean) {
        context.dataStore.edit { settings ->
            settings[HomeViewModel.MOOD_TODAY] = value
        }
    }

    fun readIsCheckMoodToday(): Flow<Boolean> {
        return context.dataStore.data
            .map {preferences ->
                preferences[HomeViewModel.MOOD_TODAY] ?: true
            }
    }

    suspend fun selectRandomFact() {
        context.dataStore.edit { settings ->
            settings[HomeViewModel.FACT_INDEX] = Random.nextInt(Constant.motivationList.size-1)
        }
    }

    fun readCounter(): Flow<Int> {
        return context.dataStore.data
            .map { preferences ->
                preferences[HomeViewModel.COUNTER] ?: 0
            }
    }

}