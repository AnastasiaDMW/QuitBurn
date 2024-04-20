package com.example.quitburn.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quitburn.Constant.motivationList
import com.example.quitburn.MainActivity
import com.example.quitburn.QuitBurnApplication
import com.example.quitburn.R
import com.example.quitburn.data.MoodEnum
import com.example.quitburn.data.PermissionManager
import com.example.quitburn.model.MoodDetails
import com.example.quitburn.model.ProgressDetail
import com.example.quitburn.model.toMood
import com.example.quitburn.model.toProgress
import com.example.quitburn.ui.theme.QuitBurnTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun HomeScreen(
    navigateToStatisticScreen: () -> Unit,
    permissionManager: PermissionManager,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelProvider(
            (LocalContext.current.applicationContext as QuitBurnApplication).moodRepository,
            (LocalContext.current.applicationContext as QuitBurnApplication).progressRepository)
    )
) {
    QuitBurnTheme {
        Scaffold(
            containerColor = colorResource(id = R.color.background),
            modifier = modifier
        ){  innerPadding ->

            HomeBody(
                navigateToStatisticScreen,
                permissionManager = permissionManager,
                viewModel = viewModel,
                modifier = modifier
                    .padding(innerPadding)

            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun HomeBody(
    navigateToStatisticScreen: () -> Unit,
    permissionManager: PermissionManager,
    viewModel: HomeViewModel,
    modifier: Modifier) {
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()
    var days by remember { mutableIntStateOf(0) }
    var isSmoker by remember { mutableStateOf(true) }
    var isCheckMoodToday by remember { mutableStateOf(false) }
    var factIndex by remember { mutableIntStateOf(0) }

    viewModel.readCounter(context).onEach { number ->
        days = number
    }.launchIn(coroutine)
    viewModel.readIsSmoker(context).onEach {value ->
        isSmoker = value
    }.launchIn(coroutine)
    viewModel.readIsCheckMoodToday(context).onEach {value ->
        isCheckMoodToday = value
        viewModel.isLoading = false
    }.launchIn(coroutine)
    viewModel.readFactIndex(context).onEach {value ->
        factIndex = value
    }.launchIn(coroutine)

    HomeContent(
        modifier = modifier,
        navigateToStatisticScreen,
        permissionManager = permissionManager,
        viewModel = viewModel,
        days = days,
        factIndex = factIndex,
        isSmoker = isSmoker,
        isCheckMoodToday = isCheckMoodToday,
        context = context
    )
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun HomeContent(
    modifier:Modifier = Modifier,
    navigateToStatisticScreen: () -> Unit,
    permissionManager: PermissionManager,
    viewModel: HomeViewModel,
    days: Int,
    factIndex: Int,
    isSmoker: Boolean,
    isCheckMoodToday: Boolean,
    context: Context
) {
    val circleColor = colorResource(id = R.color.background)
    val isVisible by remember { mutableStateOf(false) }
    var index by remember { mutableIntStateOf(0) }
    val shapeValue = remember { androidx.compose.animation.core.Animatable(1f) }
    val progress by viewModel.allProgress.observeAsState(null)
    var openAlertDialog by remember { mutableStateOf(false) }
    var isShowAlertDialog by remember { mutableStateOf(false) }
    val colorList = listOf(
        colorResource(id = R.color.accent_btn),
        colorResource(id = R.color.pro_light_blue),
        colorResource(id = R.color.blue),
        colorResource(id = R.color.pro_blue),
        colorResource(id = R.color.light_blue),
        colorResource(id = R.color.teal_200),
        colorResource(id = R.color.light_green_blue),
        colorResource(id = R.color.additional_elem),
        colorResource(id = R.color.light_green),
        colorResource(id = R.color.pro_light_green),
        colorResource(id = R.color.green),
    )
    var fullText by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        while (true) {
            shapeValue.animateTo(1f, animationSpec = tween(1500))
            shapeValue.animateTo(0.95f, animationSpec = tween(1500))
            delay(80)
        }
    }
    LaunchedEffect(Unit) {
        while (true) {
            index = colorList.indices.random()
            delay(600)
        }
    }
    val bgColor by animateColorAsState(
        colorList[index],
        animationSpec = tween(1000, easing = LinearEasing),
        label = stringResource(id = R.string.days_anim)
    )

    when {
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED -> {}
        ActivityCompat.shouldShowRequestPermissionRationale(
            context as MainActivity, Manifest.permission.POST_NOTIFICATIONS) -> {}
        else -> {
            if (!isShowAlertDialog) {
                AlertDialogPermission(
                    onConfirmation = {
                        isShowAlertDialog = !isShowAlertDialog
                        permissionManager.requestPermission(Manifest.permission.POST_NOTIFICATIONS)
                    },
                    onDismissRequest = {
                        isShowAlertDialog = !isShowAlertDialog
                        permissionManager.requestPermission(Manifest.permission.POST_NOTIFICATIONS)
                    }
                )
            }
        }
    }

    if (!viewModel.isLoading) {
        if (!isSmoker){
            if (!isCheckMoodToday) {
                if (!openAlertDialog) {
                    AlertDialogMood(
                        context = context,
                        viewModel = viewModel,
                        onDismissRequest = { openAlertDialog = false },
                        onConfirmation = {
                            openAlertDialog = true
                            runBlocking {
                                viewModel.changeValueIsCheckMoodToday(context,true)
                            }
                        }
                    )
                }
            }
        }
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        if (isSmoker) {
            HomeButtons(
                modifier = Modifier,
                onClick = {
                    runBlocking {
                        viewModel.changeValueIsSmoker(context)
                        viewModel.changeValueIsCheckMoodToday(context, false)
                    }
                    viewModel.startWorkManager(context)
                    if (progress == null) {
                        viewModel.insertProgress(ProgressDetail(
                            startDate = viewModel.getCurrentDate(),
                            maxCountDays = days,
                            countStop = 0
                        ).toProgress())
                    }
                    else {
                        val countStop = progress!!.countStop
                        viewModel.updateProgress(ProgressDetail(
                            startDate = viewModel.getCurrentDate(),
                            maxCountDays = days,
                            countStop = countStop
                        ).toProgress())
                    }
                },
                color = R.color.accent_btn,
                text = R.string.btn_start
            )
        } else {
            HomeButtons(
                modifier = Modifier,
                onClick = {

                    val countStop = progress!!.countStop + 1
                    viewModel.stopWorkManager(context)
                    viewModel.updateProgress(ProgressDetail(
                        startDate = viewModel.getCurrentDate(),
                        maxCountDays = days,
                        countStop = countStop
                    ).toProgress())
                    runBlocking {
                        viewModel.changeValueIsSmoker(context)
                        viewModel.clearCounter(context)
                        viewModel.changeValueIsCheckMoodToday(context, true)
                    }
                },
                color = R.color.additional_btn,
                text = R.string.btn_end
            )
        }
        Box(
            modifier = Modifier.padding(top = 70.dp), contentAlignment = Alignment.Center
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(188.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .size(185.dp)
                        .clickable {
                            navigateToStatisticScreen()
                        }
                ) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height
                    val canvasSize = size.minDimension
                    val radius = canvasSize / 2 * shapeValue.value

                    drawCircle(
                        color = Color.White,
                        radius = radius + 93f,
                        center = Offset(x = canvasWidth / 2, y = canvasHeight / 2)
                    )
                    drawCircle(
                        color = bgColor,
                        center = Offset(x = canvasWidth / 2, y = canvasHeight / 2),
                        radius = radius + 85f
                    )
                    drawCircle(
                        color = Color.White,
                        radius = size.minDimension / 2 + 8f,
                        center = Offset(x = canvasWidth / 2, y = canvasHeight / 2)
                    )
                    drawCircle(
                        color = circleColor,
                        center = Offset(x = canvasWidth / 2, y = canvasHeight / 2),
                        radius = size.minDimension / 2
                    )
                }
            }
            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = days.toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 60.sp
                )
                Text(
                    text = viewModel.getDayString(days),
                    fontSize = 25.sp,
                    color = Color.White
                )
            }

        }.let {
            AnimatedVisibility(visible = isVisible) { it }
        }
        Card(
            modifier = Modifier
                .wrapContentHeight()
                .padding(top = 70.dp, start = 30.dp, end = 30.dp, bottom = 10.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(id = R.color.additional_elem))
                    .animateContentSize()
                    .clickable { fullText = !fullText },
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.motivation),
                        fontSize = 20.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 5.dp, end = 25.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Image(
                            modifier = Modifier.size(28.dp),
                            painter = if (fullText) painterResource(R.drawable.arrow_up)
                                else painterResource(R.drawable.arrow_down),
                            contentDescription = stringResource(id = R.string.arrow_icon_desc)
                        )
                    }
                }
                if (fullText) {
                    Text(
                        text = if (days != 0) stringResource(motivationList[factIndex])
                        else stringResource(motivationList[27]),
                        fontSize = 19.sp,
                        color = Color.White,
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp,
                            top = 0.dp
                        )
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun HomeButtons(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    @ColorRes color: Int,
    @StringRes text: Int
) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 30.dp, end = 30.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = color)
        ),
        onClick = { onClick() }
    ) {
        Text(
            text = stringResource(id = text),
            color = Color.White,
            fontSize = 21.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AlertDialogMood(
    context: Context,
    viewModel: HomeViewModel,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    var mood by remember { mutableStateOf(MoodEnum.HAPPY) }

    AlertDialog(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        containerColor = colorResource(R.color.accent_btn),
        title = {
            Text(
                text = stringResource(R.string.dialog_mood_head),
                color = Color.White,
                fontSize = 22.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        modifier = Modifier
                            .size(65.dp)
                            .padding(start = 5.dp, end = 5.dp)
                            .clickable { mood = MoodEnum.STAR },
                        painter = painterResource(R.drawable.star),
                        contentDescription = stringResource(R.string.star_mood)
                    )
                    Image(
                        modifier = Modifier
                            .size(65.dp)
                            .padding(start = 5.dp, end = 5.dp)
                            .clickable { mood = MoodEnum.SMILE },
                        painter = painterResource(R.drawable.smile),
                        contentDescription = stringResource(R.string.star_mood)
                    )
                    Image(
                        modifier = Modifier
                            .size(65.dp)
                            .padding(start = 5.dp, end = 5.dp)
                            .clickable { mood = MoodEnum.HAPPY },
                        painter = painterResource(R.drawable.happy),
                        contentDescription = stringResource(R.string.star_mood)
                    )
                    Image(
                        modifier = Modifier
                            .size(65.dp)
                            .padding(start = 5.dp, end = 5.dp)
                            .clickable { mood = MoodEnum.DISAPPOINTED },
                        painter = painterResource(R.drawable.disappointed),
                        contentDescription = stringResource(R.string.star_mood)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        modifier = Modifier
                            .size(65.dp)
                            .padding(start = 5.dp, end = 5.dp)
                            .clickable { mood = MoodEnum.MAD },
                        painter = painterResource(R.drawable.mad),
                        contentDescription = stringResource(R.string.star_mood)
                    )
                    Image(
                        modifier = Modifier
                            .size(65.dp)
                            .padding(start = 5.dp, end = 5.dp)
                            .clickable { mood = MoodEnum.CRY },
                        painter = painterResource(R.drawable.cry),
                        contentDescription = stringResource(R.string.star_mood)
                    )
                    Image(
                        modifier = Modifier
                            .size(65.dp)
                            .padding(start = 5.dp, end = 5.dp)
                            .clickable { mood = MoodEnum.NAUSEA },
                        painter = painterResource(R.drawable.nausea),
                        contentDescription = stringResource(R.string.star_mood)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(id = mood.desc),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        confirmButton = {
            Button(
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.background)
                ),
                onClick = {
                    val currentDate = viewModel.getCurrentDate()
                    val moodName = context.getString(mood.desc)
                    viewModel.insertMood(
                        MoodDetails(
                            date = currentDate,
                            moodName = moodName
                        ).toMood())
                    onConfirmation()
                }
            ) {
                Text(
                    stringResource(R.string.save_btn),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        onDismissRequest = { onDismissRequest() }
    )
}

@Composable
fun AlertDialogPermission(
    onConfirmation: () -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        containerColor = colorResource(R.color.additional_elem),
        title = {
            Text(
                modifier = Modifier.width(300.dp),
                text = stringResource(R.string.dialog_permission_title),
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.statistic),
                    contentDescription = stringResource(R.string.dialog_permission_desc)
                )
            }
        },
        confirmButton = {
            Button(
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.accent_btn)
                ),
                onClick = { onConfirmation() }
            ) {
                Text(
                    text = stringResource(R.string.btn_next),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        onDismissRequest = { onDismissRequest() }
    )
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Preview(showBackground = true)
@Composable
fun HomeBodyPreview() {
    QuitBurnTheme {
        HomeContent(
            Modifier,
            {},
            PermissionManager(LocalContext.current as MainActivity),
            viewModel(),  2, 28, isSmoker = true,
            isCheckMoodToday = true,
            context = LocalContext.current
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AlertDialogMoodPreview() {
    QuitBurnTheme {
        AlertDialogMood(LocalContext.current, viewModel(), {},{})
    }
}

@Preview(showBackground = true)
@Composable
fun AlertDialogPermissionPreview() {
    QuitBurnTheme {
        AlertDialogPermission({},{})
    }
}