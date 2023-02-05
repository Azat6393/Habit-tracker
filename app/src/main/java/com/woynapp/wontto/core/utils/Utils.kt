package com.woynapp.wontto.core.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.woynapp.wontto.R
import java.text.SimpleDateFormat
import java.util.*


fun showAlertDialog(
    context: Context,
    title: String,
    message: String,
    onPositive: () -> Unit
) {
    val alert = AlertDialog.Builder(context)
    alert.setTitle(title)
    alert.setMessage(message)
    alert.setPositiveButton(context.getString(R.string.yes)) { _, _ ->
        onPositive()
    }
    alert.setNegativeButton(context.getString(R.string.no)) { _, _ -> }
    alert.show()
}


fun requestPermission(
    context: Activity,
    view: View,
    permission: String,
    message: String,
    granted: (Boolean) -> Unit,
    shouldShorPer: () -> Unit
) {
    when {
        ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED -> {
            // Permission is granted
            granted(true)
        }
        ActivityCompat.shouldShowRequestPermissionRationale(
            context,
            permission
        ) -> {
            // Additional rationale should be displayed
            Snackbar.make(
                view,
                message,
                Snackbar.LENGTH_INDEFINITE
            ).setAction(context.getString(R.string.ok)) {
                shouldShorPer()
            }.show()
        }
        else -> {
            // Permission has not been asked yet
            granted(false)
        }
    }
}


fun getJsonFromAssets(context: Context, fileName: String): String? {
    var jsonString = ""
    try {
        val inputStream = context.assets.open(fileName)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        jsonString = String(buffer)
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
    return jsonString
}


@SuppressLint("SimpleDateFormat")
fun parseHourAndMinute(date: Long): String {
    val formatter = SimpleDateFormat("k:mm")
    val calendar: Calendar = Calendar.getInstance()
    calendar.timeInMillis = date
    return formatter.format(calendar.time)
}

fun randomId(): Int {
    return (0..999999).random()
}