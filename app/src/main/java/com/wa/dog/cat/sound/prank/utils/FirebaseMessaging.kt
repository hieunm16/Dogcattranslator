package com.wa.dog.cat.sound.prank.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION_CODES.O
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.wa.dog.cat.sound.prank.R
import com.wa.dog.cat.sound.prank.activities.SplashActivity
import com.wa.dog.cat.sound.prank.extension.isAtLeastSdkVersion

class FirebaseMessaging : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        if (message.notification != null) {
            notificationGenerate(message.notification?.title!!, message.notification?.body!!)
        }
    }


    @SuppressLint("RemoteViewLayout")
    private fun getRemoteView(title: String, content: String): RemoteViews {
        val remoteViews = RemoteViews(
            "com.wa.dog.cat.sound.prank",
            R.layout.layout_notification
        )
        remoteViews.setTextViewText(R.id.tvTitle, title)
        remoteViews.setTextViewText(R.id.tvContent, content)
        remoteViews.setImageViewResource(R.id.imLogo, R.mipmap.ic_launcher)

        return remoteViews
    }

    private fun notificationGenerate(title: String, content: String) {

        val intent = Intent(this, SplashActivity::class.java)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_MUTABLE
        )

        var builder: NotificationCompat.Builder =
            NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)

        builder = builder.setContent(getRemoteView(title, content))

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (isAtLeastSdkVersion(O)) {
            val notificationChannel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(0, builder.build())

    }

}

const val channelId = "notification_channel"
const val channelName = "com.wa.dog.cat.sound.prank.utils"
