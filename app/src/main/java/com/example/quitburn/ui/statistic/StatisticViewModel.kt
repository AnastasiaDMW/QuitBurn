package com.example.quitburn.ui.statistic

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.quitburn.R
import com.example.quitburn.model.DiagramMood
import com.example.quitburn.model.Mood
import com.example.quitburn.model.Progress
import com.example.quitburn.repository.MoodRepository
import com.example.quitburn.repository.ProgressRepository

class StatisticViewModel(
    private val repositoryMood: MoodRepository,
    private val repositoryProgress: ProgressRepository
): ViewModel() {

    val allProgress: LiveData<Progress> = repositoryProgress.getProgress().asLiveData()
    val allMood: LiveData<List<Mood>> = repositoryMood.getAllMood().asLiveData()

    private val _diagramList = mutableStateListOf<DiagramMood>()
    val diagramList : List<DiagramMood> = _diagramList
    private val _moodCountList = mutableStateListOf<Int>()
    val moodCountList : List<Int> = _moodCountList

    fun getDataForDiagram(allMood: List<Mood>, context: Context) {
        _moodCountList.addAll(listOf(0,0,0,0,0,0,0))
        for(mood in allMood) {
            if (mood.moodName == context.getString(R.string.nausea_mood)){
                _moodCountList[0] += 1
            }
            else if (mood.moodName == context.getString(R.string.cry_mood)) {
                _moodCountList[1] += 1
            }
            else if (mood.moodName == context.getString(R.string.mad_mood)) {
                _moodCountList[2] += 1
            }
            else if (mood.moodName == context.getString(R.string.disappointed_mood)) {
                _moodCountList[3] += 1
            }
            else if (mood.moodName == context.getString(R.string.happy_mood)) {
                _moodCountList[4] += 1
            }
            else if (mood.moodName == context.getString(R.string.smile_mood)) {
                _moodCountList[5] += 1
            }
            else {
                _moodCountList[6] += 1
            }
        }
        _diagramList.addAll(
            listOf(
                DiagramMood(
                    1, ContextCompat.getDrawable(context, R.drawable.nausea)!!,
                        3L, _moodCountList[0].toLong(), context.getColor(R.color.diagram_nausea)),
                DiagramMood(2, ContextCompat.getDrawable(context, R.drawable.cry)!!,
                    6L, _moodCountList[1].toLong(), context.getColor(R.color.diagram_cry)),
                DiagramMood(3, ContextCompat.getDrawable(context, R.drawable.mad)!!,
                    9L, _moodCountList[2].toLong(), context.getColor(R.color.diagram_mad)),
                DiagramMood(4, ContextCompat.getDrawable(context, R.drawable.disappointed)!!,
                    12L, _moodCountList[3].toLong(), context.getColor(R.color.diagram_dissappointed)),
                DiagramMood(5, ContextCompat.getDrawable(context, R.drawable.happy)!!,
                    15L, _moodCountList[4].toLong(), context.getColor(R.color.diagram_happy)),
                DiagramMood(6, ContextCompat.getDrawable(context, R.drawable.smile)!!,
                    18L, _moodCountList[5].toLong(), context.getColor(R.color.diagram_smile)),
                DiagramMood(7, ContextCompat.getDrawable(context, R.drawable.star)!!,
                    21L, _moodCountList[6].toLong(), context.getColor(R.color.diagram_star))
            )
        )
    }

}

class StatisticViewModelProvider(
    private val repositoryMood: MoodRepository,
    private val repositoryProgress: ProgressRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticViewModel::class.java)){
            return StatisticViewModel(repositoryMood, repositoryProgress) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}