package com.example.quitburn.util

object AppStateManager {
    private var isAppInForeground = false

    fun setAppInForeground(isInForeground: Boolean) {
        isAppInForeground = isInForeground
    }

    fun isAppInForeground(): Boolean {
        return isAppInForeground
    }
}