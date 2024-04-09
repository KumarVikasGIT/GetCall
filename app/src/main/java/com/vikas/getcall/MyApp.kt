package com.vikas.getcall

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat


class MyApp: Application() {

//    companion object NotificationChannels{
//        const val CHANNEL_ID = "my_channel_id"
//        const val CHANNEL_NAME = "My Channel"
//        const val CHANNEL_DESCRIPTION = "This is a notification channel"
//        const val CHANNEL_IMPORTANCE = NotificationManager.IMPORTANCE_HIGH
//
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        createNotificationChannel()
//    }
//
//    private fun createNotificationChannel() {
//        val channel = NotificationChannel(
//            CHANNEL_ID,
//            CHANNEL_NAME,
//            CHANNEL_IMPORTANCE
//        )
//        channel.description = CHANNEL_DESCRIPTION
//        val notificationManager = getSystemService(
//            NotificationManager::class.java
//        )
//        notificationManager.createNotificationChannel(channel)
//    }
}


class NotificationUtils(context: Context) {
    private val mContext: Context

    init {
        mContext = context
    }

fun showFullScreenNotification(title: String?, message: String?) {
        val notificationManager =
            mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a notification channel for devices running Android Oreo and above
        val channel = NotificationChannel(
            "channel_id",
            "Channel Name",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)

        // Create the notification builder
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(mContext, "channel_id")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setFullScreenIntent(fullScreenIntent, true)

        // Show the notification
        notificationManager.notify(0, builder.build())
    }

    private val fullScreenIntent: PendingIntent
        private get() {
            val intent: Intent = Intent(mContext, CallReceiver::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            return PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }
}

