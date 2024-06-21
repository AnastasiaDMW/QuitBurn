package com.example.quitburn.model

import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color

data class DiagramMood(
    var id: Long,
    var smileImage: Drawable,
    var distance: Long,
    var count: Long,
    var color: Int
)
