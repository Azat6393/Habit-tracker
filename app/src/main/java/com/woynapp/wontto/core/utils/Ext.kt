package com.woynapp.wontto.core.utils

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.woynapp.wontto.domain.model.Emoji
import com.woynapp.wontto.domain.model.Motivation


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

fun String.fromJsonToEmoji(): List<Emoji> {
    val gson = Gson()
    return gson.fromJson(this, Array<Emoji>::class.java).asList()
}

fun String.fromJsonToMotivation(): List<Motivation> {
    val gson = Gson()
    return gson.fromJson(this, Array<Motivation>::class.java).asList()
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