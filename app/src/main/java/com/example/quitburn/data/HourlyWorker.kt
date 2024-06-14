package com.example.quitburn.data

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.BackoffPolicy
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import com.example.quitburn.Constant.CHANNEL_ID
import com.example.quitburn.Constant.NOTIFICATION_ID
import com.example.quitburn.MainActivity
import com.example.quitburn.QuitBurnApplication
import com.example.quitburn.R
import com.example.quitburn.repository.MoodRepository
import com.example.quitburn.repository.ProgressRepository
import com.example.quitburn.ui.home.HomeViewModel
import com.example.quitburn.util.AppStateManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class HourlyWorker(
    private val context: Context,
    private val parameters: WorkerParameters
): CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {

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

        var days = 0
        var isCheckToday = false
        val dataStoreManager = DataStoreManager(context)
        CoroutineScope(Dispatchers.IO).launch {
            dataStoreManager.changeValueIsCheckMoodToday(false)
            dataStoreManager.readIsCheckMoodToday().collect {isCheck ->
                isCheckToday = isCheck
                dataStoreManager.incrementDays()
                dataStoreManager.selectRandomFact()
                dataStoreManager.readCounter().collect { count ->
                    days = count
                    if (!isCheckToday) {
                        if (count > 1) {
                            if (!AppStateManager.isAppInForeground()) {
                                createNotificationChannel(applicationContext)
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
        }

        return Result.success()
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "channel"
            val descriptionText = "mood channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}