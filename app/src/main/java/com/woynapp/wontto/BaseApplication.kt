package com.woynapp.wontto

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.woynapp.wontto.core.utils.Constants
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication : Application(){


    override fun onCreate() {
        super.onCreate()
        val mSharedPreferences =
            getSharedPreferences(Constants.PREFERENCE_DATABASE_NAME, Context.MODE_PRIVATE)
        val isDarkMode = mSharedPreferences.getBoolean(Constants.PREFERENCE_DARK_MODE, false)
        if (isDarkMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            setTheme(R.style.Theme_Aliskanlik_Dark)
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            setTheme(R.style.Theme_Aliskanlik_Light)
        }

        FirebaseMessaging.getInstance().subscribeToTopic("allDevices")

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Log.d("FCM", token)
            println(token)
        })
    }
}