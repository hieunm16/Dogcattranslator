package com.wa.dog.cat.sound.prank.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.wa.dog.cat.sound.prank.R
import com.wa.dog.cat.sound.prank.activities.SplashActivity
import com.wa.dog.cat.sound.prank.extension.isAtLeastSdkVersion

class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        notificationGenerate()
        return Result.success()
    }

    private fun notificationGenerate() {
        val titleDailyNotification: String =
            if (com.wa.dog.cat.sound.prank.utils.device.DeviceUtils.checkInternetConnection(applicationContext)) FirebaseRemoteConfig.getInstance()
                .getString("title_daily_notification") else applicationContext.getString(R.string.title_daily_notification)

        val contentDailyNotification =
            if (com.wa.dog.cat.sound.prank.utils.device.DeviceUtils.checkInternetConnection(applicationContext)) FirebaseRemoteConfig.getInstance()
                .getString("content_daily_notification") else applicationContext.getString(R.string.content_daily_notification)


        val intent = Intent(applicationContext, SplashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_MUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, channelWorkId)
            .setSmallIcon(R.drawable.ic_cat)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
            .setOnlyAlertOnce(true)
            .setContentTitle(titleDailyNotification)
            .setContentText(contentDailyNotification)
            .setContentIntent(pendingIntent)

        if (isAtLeastSdkVersion(Build.VERSION_CODES.O)) {
            val channelDescription = "channel Description"
            val notificationChannel =
                NotificationChannel(
                    channelWorkId,
                    channelWorkName,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = channelDescription
                }

            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(notificationChannel)

            with(NotificationManagerCompat.from(applicationContext)) {
                if (ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                notify(1, notification.build())
            }
        }
    }
}

const val channelWorkId = "remind_channel"
const val channelWorkName = "com.wa.dog.cat.sound.prank.utils"