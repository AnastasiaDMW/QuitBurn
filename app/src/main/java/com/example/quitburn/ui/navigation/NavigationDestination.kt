package com.example.quitburn.ui.navigation

import com.example.quitburn.R

interface NavigationDestination {
    val route: String
    val titleRes: Int
}

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

object StatisticDestination : NavigationDestination {
    override val route = "item_entry"
    override val titleRes = R.string.statistic
}