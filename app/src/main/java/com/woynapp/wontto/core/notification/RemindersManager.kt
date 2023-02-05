package com.woynapp.wontto.core.notification

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.woynapp.wontto.domain.model.AlarmItem
import com.woynapp.wontto.domain.model.Category
import java.util.*

object RemindersManager {

    @SuppressLint("UnspecifiedImmutableFlag")
    fun startReminder(
        context: Context,
        item: AlarmItem
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent =
            Intent(context.applicationContext, AlarmReceiver::class.java).let { intent ->
                intent.putExtra("reminder_id", item.uuid)
                intent.putExtra("reminder_time", item.time)
                intent.putExtra("message", item.message)
                intent.putExtra("reminder_is_mute", item.is_mute)
                intent.putExtra("reminder_habit_id", item.habit_id)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.getBroadcast(
                        context.applicationContext,
                        item.uuid,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                    )
                } else {
                    PendingIntent.getBroadcast(
                        context.applicationContext,
                        item.uuid,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }
            }

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = item.time
        }

        if (Calendar.getInstance(Locale.getDefault())
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
        item: AlarmItem
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(
                context,
                item.uuid,
                intent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE
                else PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        alarmManager.cancel(intent)
    }
}