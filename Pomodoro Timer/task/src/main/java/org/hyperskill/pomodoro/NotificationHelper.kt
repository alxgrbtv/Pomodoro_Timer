package org.hyperskill.pomodoro

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationHelper {
    companion object {
        fun createNotificationChannel(context: Context, importance: Int, name: String, description: String) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelId = "${context.packageName}-$name"
                val channel = NotificationChannel(channelId, name, importance).apply {
                    this.description = description
                }

                // Register the channel with the system
                val notificationManager =
                        context.getSystemService(NotificationManager::class.java) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }

        fun createNotification(context: Context, title: String, message: String, autoCancel: Boolean) {
            // Create an explicit intent for an Activity in your app
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            val channelId = "${context.packageName}-${context.getString(R.string.app_name)}"

            val notificationBuilder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(autoCancel)

            // Show the notification
            with(NotificationManagerCompat.from(context)) {
                // notificationId is a unique int for each notification that you must define
                val notificationId = 1001
                notify(notificationId, notificationBuilder.build())
            }
        }
    }
}