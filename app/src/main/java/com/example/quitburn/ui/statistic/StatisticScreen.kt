package com.example.quitburn.ui.statistic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quitburn.QuitBurnApplication
import com.example.quitburn.R
import com.example.quitburn.ui.theme.QuitBurnTheme

@Composable
fun StatisticScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StatisticViewModel = viewModel(
        factory = StatisticViewModelProvider(
            (LocalContext.current.applicationContext as QuitBurnApplication).moodRepository,
            (LocalContext.current.applicationContext as QuitBurnApplication).progressRepository)
    )
) {
    QuitBurnTheme {
        Scaffold(
            containerColor = colorResource(id = R.color.background),
            modifier = modifier
        ){  innerPadding ->

            StatisticBody(
                viewModel = viewModel,
                modifier = modifier
                    .padding(innerPadding)

            )
        }
    }
}

@Composable
fun StatisticBody(
    viewModel: StatisticViewModel,
    modifier: Modifier
) {
    val moods by viewModel.allMood.observeAsState(null)
    val progress by viewModel.allProgress.observeAsState(null)

    Column() {

    }

}