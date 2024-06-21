package com.example.quitburn.ui.statistic

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quitburn.QuitBurnApplication
import com.example.quitburn.R
import com.example.quitburn.model.DiagramMood
import com.example.quitburn.ui.theme.QuitBurnTheme
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.MPPointF
import java.io.ByteArrayOutputStream

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
    val context = LocalContext.current
    val moods by viewModel.allMood.observeAsState(null)
    val progress by viewModel.allProgress.observeAsState(null)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(6.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp),
            horizontalAlignment = Alignment.Start
        ){
            Spacer(modifier = Modifier.height(25.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.mood_statistic),
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Image(
                    modifier = Modifier
                        .width(40.dp)
                        .clickable { },
                    painter = painterResource(id = R.drawable.question), // Поменять картинку
                    contentDescription = context.getString(R.string.help_user)
                )
            }

        }
        if (viewModel.diagramList.isNotEmpty()) {
            StatisticsBarChart(viewModel.diagramList, context)
        } else {
            if (moods?.isNotEmpty() == true){
                viewModel.getDataForDiagram(moods!!, context)
            }
        }

    }
}

@Composable
fun StatisticsBarChart(
    diagramList: List<DiagramMood>,
    context: Context
) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(start = 2.dp, end = 2.dp, top = 30.dp, bottom = 0.dp),
        factory = { context ->
            BarChart(context).apply {
                setDrawGridBackground(false)
                setDrawBorders(false)
                setPinchZoom(true)
                isScaleXEnabled = false
                isScaleYEnabled = false
                isDragEnabled = true
                axisRight.isEnabled = false
                axisLeft.isEnabled = false
                description.isEnabled = false
                legend.isEnabled = false
            }
        },
        update = { barChart ->
            val entries = diagramList.map { statistic ->
                val scaledDrawable = statistic.smileImage?.let { drawable ->
                    val bitmap = context.reduceImageSize(drawable)
                    BitmapDrawable(context.resources, bitmap)
                }
                BarEntry(
                    statistic.distance.toFloat(),
                    statistic.count.toFloat(),
                    scaledDrawable.also { it?.setBounds(it.bounds.left, it.bounds.top - 55, it.bounds.right, it.bounds.bottom - 10) } // Смещаем изображение на 20 единиц вверх
                )
            }.toMutableList() as MutableList<BarEntry>


            val colors = diagramList.map { it.color }.toIntArray()

            val dataSet = BarDataSet(entries, "").apply {
                setDrawIcons(true)
                setColors(*colors)
                iconsOffset = MPPointF.getInstance(0f, 0f)
                setDrawValues(false)
            }

            val barData = BarData(dataSet).apply {
                barWidth = 2.5f
            }

            barChart.data = barData
            barChart.setFitBars(true)
            barChart.xAxis.isEnabled = true
            barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            barChart.xAxis.setDrawGridLines(false)
            barChart.xAxis.setDrawLabels(false)
            barChart.xAxis.setDrawAxisLine(true)
            barChart.xAxis.axisLineColor = Color.LightGray.toArgb()
            barChart.xAxis.axisLineWidth = 11f
            barChart.invalidate()
        }
    )
    val paddingText = 10.dp
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 28.dp, end = 28.dp, top = 0.dp, bottom = 0.dp)
            .offset(y = -paddingText),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        for (statistic in diagramList){
            CustomText(statistic.count.toString())
        }
    }
}

@Composable
fun CustomText(
    number: String,
    modifier: Modifier = Modifier
) {
    var width = 0.dp
    if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        width = when (number.length) {
            in 100..999 -> 50.dp
            else -> 90.dp
        }
    } else {
        width = when (number.length) {
            in 100..999 -> 42.dp
            else -> 30.dp
        }
    }

    Column(
        modifier = modifier.width(width),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = number,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

fun Context.reduceImageSize(drawable: Drawable?): Bitmap? {
    if (drawable == null) {
        return null
    }

    val baos = ByteArrayOutputStream()
    val bitmap = drawable.toBitmap(120,120, Bitmap.Config.ARGB_8888)
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
    val imageBytes: ByteArray = baos.toByteArray()

    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}