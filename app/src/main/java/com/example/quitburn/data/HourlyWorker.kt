package com.example.quitburn.data

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.quitburn.Constant.CHANNEL_ID
import com.example.quitburn.Constant.NOTIFICATION_ID
import com.example.quitburn.MainActivity
import com.example.quitburn.QuitBurnApplication
import com.example.quitburn.R
import com.example.quitburn.repository.MoodRepository
import com.example.quitburn.repository.ProgressRepository
import com.example.quitburn.ui.home.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HourlyWorker(appContext: Context, workerParams: WorkerParameters): Worker(appContext, workerParams) {

    override fun doWork(): Result {

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(applicationContext.getString(R.string.notification_desc))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val viewModel = HomeViewModel(
            MoodRepository(QuitBurnApplication().database.moodDao()),
            ProgressRepository(QuitBurnApplication().database.progressDao())
        )
        var days = 0
        var isCheckToday = false
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.changeValueIsCheckMoodToday(applicationContext, false)
            viewModel.readIsCheckMoodToday(applicationContext).collect { isCheck ->
                isCheckToday = isCheck

                viewModel.incrementCounter(applicationContext)
                viewModel.selectRandomFact(applicationContext)
                viewModel.readCounter(applicationContext).collect { count ->
                    days = count
                    if (!isCheckToday) {
                        if (count > 1) {
                            viewModel.createNotificationChannel(applicationContext)
                            with(NotificationManagerCompat.from(applicationContext)) {
                                if (ActivityCompat.checkSelfPermission(
                                        applicationContext,
                                        Manifest.permission.POST_NOTIFICATIONS
                                    ) != PackageManager.PERMISSION_GRANTED
                                ) {
                                    return@with
                                }
                                notify(NOTIFICATION_ID, builder.build())
                            }
                        }
                    }
                }
            }
        }
        return Result.success()
    }
}