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

class RemindersManager(private val context: Context) {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    @SuppressLint("UnspecifiedImmutableFlag")
    fun startReminder(
        item: AlarmItem
    ) {
        val intent =
            Intent(context, AlarmReceiver::class.java).apply {
                putExtra("message", item.message)
                putExtra("uuid", item.uuid)
            }
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            item.time,
            AlarmManager.INTERVAL_DAY,
            PendingIntent.getBroadcast(
                context,
                item.uuid,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun stopReminder(
        item: AlarmItem
    ) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                item.uuid,
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}