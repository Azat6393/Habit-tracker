package com.woynapp.aliskanlik.core.utils

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import java.util.concurrent.TimeUnit


fun Long.toDays(): Long {
    return ((this / (1000 * 60 * 60 * 24)))
}

fun Context.showToastMessage(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}


fun String.fromJsonToCountyList(): List<CountryInfo> {
    val gson = Gson()
    return gson.fromJson(this, Array<CountryInfo>::class.java).asList()
}

fun Context.checkPermission(permission: String): Int {
    if (ContextCompat.checkSelfPermission(
            this, permission
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        return 0
    }
    return -1
}