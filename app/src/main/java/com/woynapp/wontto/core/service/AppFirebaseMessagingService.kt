package com.woynapp.wontto.core.service


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.woynapp.wontto.R
import com.woynapp.wontto.presentation.AuthActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AppFirebaseMessagingService : FirebaseMessagingService() {

    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    private lateinit var builder: Notification.Builder


    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.data["data_title"]
        val text = message.data["data_text"]

        val CHANNEL_ID = "HABIT_NOTIFICATION"
        val intent = Intent(this, AuthActivity::class.java)

        val pendingIntent =
            PendingIntent.getActivity(
                this, 0, intent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE
                else PendingIntent.FLAG_UPDATE_CURRENT
            )


        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(
                CHANNEL_ID,
                "${getString(R.string.app_name)} notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(text)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        this.resources,
                        R.drawable.ic_launcher_background
                    )
                )
                .setContentIntent(pendingIntent)
        } else {
            builder = Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(text)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        this.resources,
                        R.drawable.ic_launcher_background
                    )
                )
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(1, builder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}