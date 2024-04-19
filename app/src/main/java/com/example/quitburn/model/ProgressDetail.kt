package com.example.quitburn.model

data class ProgressDetail(
    val id: Int = 1,
    val startDate: String = "",
    val maxCountDays: Int = 0,
    val countStop: Int = 0
)

fun ProgressDetail.toProgress(): Progress = Progress(
    id = id,
    startDate = startDate,
    maxCountDays = maxCountDays,
    countStop = countStop
)