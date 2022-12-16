package com.woynapp.wontto.core.notification

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.*

object RemindersManager {

    @SuppressLint("UnspecifiedImmutableFlag")
    fun startReminder(
        context: Context,
        reminderTime: String,
        reminderId: Int,
        message: String
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val (hours, min) = reminderTime.split(":").map { it.toInt() }
        val intent =
            Intent(context.applicationContext, AlarmReceiver::class.java).let { intent ->
                intent.putExtra("reminder_id", reminderId)
                intent.putExtra("reminder_time", reminderTime)
                intent.putExtra("message",message)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.getBroadcast(
                        context.applicationContext,
                        reminderId,
                        intent,
                        PendingIntent.FLAG_MUTABLE
                    )
                } else {
                    PendingIntent.getBroadcast(
                        context.applicationContext,
                        reminderId,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }
            }

        val calendar: Calendar = Calendar.getInstance(Locale.ENGLISH).apply {
            set(Calendar.HOUR_OF_DAY, hours)
            set(Calendar.MINUTE, min)
        }

        if (Calendar.getInstance(Locale.ENGLISH)
                .apply { add(Calendar.MINUTE, 1) }.timeInMillis - calendar.timeInMillis > 0
        ) {
            calendar.add(Calendar.DATE, 1)
        }

        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(calendar.timeInMillis, intent),
            intent
        )
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun stopReminder(
        context: Context,
        reminderId: Int
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(
                context,
                reminderId,
                intent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE
                else PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        alarmManager.cancel(intent)
    }
}