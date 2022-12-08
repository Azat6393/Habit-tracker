package com.woynapp.aliskanlik.core.utils

import android.app.AlertDialog
import android.content.Context
import com.woynapp.aliskanlik.R


fun getDaysDetail(days: List<Boolean>, startedMl: Long): List<Int> {
    val intList = arrayListOf<Int>()
    val currentMl = System.currentTimeMillis()
    val currentDayFromMl = currentMl.toDays()
    val startedDayFromMl = startedMl.toDays()
    if (currentDayFromMl > startedDayFromMl) {
        val currentDay = currentDayFromMl - startedDayFromMl
        days.forEachIndexed { index, _ ->
            val day = index + 1
            print("$day, ")
            if (days[index]) {
                intList.add(1)
            } else if (!days[index] && day < currentDay) {
                intList.add(2)
            } else {
                intList.add(0)
            }
        }
    } else {
        days.forEachIndexed { index, _ ->
            if (days[index]) {
                intList.add(1)
            } else {
                intList.add(0)
            }
        }
    }
    return intList
}


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