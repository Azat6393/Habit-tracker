package com.woynapp.wontto.core.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import com.woynapp.wontto.R

class AlarmReceiver : BroadcastReceiver() {

    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    private lateinit var builder: Notification.Builder

    override fun onReceive(context: Context, intent: Intent) {

        val message = intent.getStringExtra("message")
            ?: context.getString(R.string.description_notification_reminder)
        val uuid = intent.getIntExtra("uuid", 0)

        val pendingIntent =
            PendingIntent.getActivity(
                context, 0, intent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE
                else PendingIntent.FLAG_UPDATE_CURRENT
            )

        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationChannel = NotificationChannel(
            context.getString(R.string.reminders_notification_channel_id),
            context.getString(R.string.reminders_notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.GREEN
        notificationChannel.enableVibration(false)
        notificationManager.createNotificationChannel(notificationChannel)

        builder = Notification.Builder(context, context.getString(R.string.reminders_notification_channel_id))
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(message)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.ic_launcher_background
                )
            )
            .setContentIntent(pendingIntent)
        notificationManager.notify(1, builder.build())
    }
}

