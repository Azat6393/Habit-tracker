package com.woynapp.wontto.core.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.woynapp.wontto.R

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            val id = intent.getIntExtra("reminder_id", 0)
            val time = intent.getStringExtra("reminder_time")
            val message = intent.getStringExtra("message")
                ?: context.getString(R.string.description_notification_reminder)
            time?.let { RemindersManager.startReminder(context, it, id, message) }
        }
    }
}