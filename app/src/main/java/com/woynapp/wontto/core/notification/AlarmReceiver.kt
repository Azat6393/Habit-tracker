package com.woynapp.wontto.core.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.woynapp.wontto.R
import com.woynapp.wontto.domain.model.AlarmItem
import com.woynapp.wontto.presentation.MainActivity

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        val message = intent.getStringExtra("message")
            ?: context.getString(R.string.description_notification_reminder)
        notificationManager.sendReminderNotification(
            applicationContext = context,
            channelId = context.getString(R.string.reminders_notification_channel_id),
            message
        )
        val id = intent.getIntExtra("reminder_id", 0)
        val time = intent.getLongExtra("reminder_time", 0)
        val isMute = intent.getBooleanExtra("reminder_is_mute", false)
        val habitId = intent.getStringExtra("reminder_habit_id") ?: ""
        RemindersManager.startReminder(
            context = context.applicationContext,
            item = AlarmItem(
                uuid = id,
                message = message,
                time = time,
                is_mute = isMute,
                habit_id = habitId
            )
        )
    }
}

fun NotificationManager.sendReminderNotification(
    applicationContext: Context,
    channelId: String,
    message: String
) {
    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        applicationContext,
        1,
        contentIntent,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE
        else PendingIntent.FLAG_UPDATE_CURRENT
    )
    val builder = NotificationCompat.Builder(applicationContext, channelId)
        .setContentTitle(applicationContext.getString(R.string.title_notification_reminder))
        .setContentText(message)
        .setSmallIcon(R.drawable.notification)
        .setStyle(
            NotificationCompat.BigTextStyle()
                .bigText(message)
        )
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    notify(NOTIFICATION_ID, builder.build())
}

const val NOTIFICATION_ID = 1