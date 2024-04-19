package com.example.quitburn.model

data class MoodDetails(
    val id: Int = 0,
    val date:String = "",
    val moodName: String = ""
)

fun MoodDetails.toMood(): Mood = Mood(
    id = id,
    date = date,
    moodName = moodName
)