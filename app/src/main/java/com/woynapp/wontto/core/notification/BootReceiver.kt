package com.woynapp.wontto.core.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.woynapp.wontto.R
import com.woynapp.wontto.domain.model.AlarmItem

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            val id = intent.getIntExtra("reminder_id", 0)
            val time = intent.getLongExtra("reminder_time", 0)
            val message = intent.getStringExtra("message")
                ?: context.getString(R.string.description_notification_reminder)
            val is_mute = intent.getBooleanExtra("reminder_is_mute", false)
            val habit_id = intent.getStringExtra("reminder_habit_id") ?: ""
            val item = AlarmItem(
                uuid = id,
                time = time,
                message = message,
                is_mute = is_mute,
                habit_id = habit_id
            )
            RemindersManager.startReminder(context, item)
        }
    }
}