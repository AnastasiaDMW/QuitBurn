package com.example.quitburn.ui.statistic

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.quitburn.model.Mood
import com.example.quitburn.model.Progress
import com.example.quitburn.repository.OfflineMoodRepository
import com.example.quitburn.repository.OfflineProgressRepository
import com.example.quitburn.ui.home.HomeViewModel

class StatisticViewModel(
    private val repositoryMood: OfflineMoodRepository,
    private val repositoryProgress: OfflineProgressRepository
): ViewModel() {

    val allProgress: LiveData<Progress> = repositoryProgress.getProgress().asLiveData()
    val allMood: LiveData<List<Mood>> = repositoryMood.getAllMood().asLiveData()

}

class StatisticViewModelProvider(
    private val repositoryMood: OfflineMoodRepository,
    private val repositoryProgress: OfflineProgressRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticViewModel::class.java)){
            return StatisticViewModel(repositoryMood, repositoryProgress) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}