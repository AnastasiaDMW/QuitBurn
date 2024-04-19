package com.example.quitburn.model

data class ProgressDetail(
    val id: Int = 0,
    val startDate: String = "",
    val countStop: Int = 0
)

fun ProgressDetail.toProgress(): Progress = Progress(
    id = id,
    startDate = startDate,
    countStop = countStop
)