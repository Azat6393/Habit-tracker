package com.woynapp.aliskanlik.core.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            val id = intent.getIntExtra("reminder_id", 0)
            val time = intent.getStringExtra("reminder_time")
            time?.let { RemindersManager.startReminder(context, it, id) }
        }
    }
}