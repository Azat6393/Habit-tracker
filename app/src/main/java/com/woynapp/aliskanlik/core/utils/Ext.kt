package com.woynapp.aliskanlik.core.utils

import android.content.Context
import android.widget.Toast


fun Long.toDays(): Int{
    return ((this / (1000*60*60*24)) % 7).toInt()
}

fun Context.showToastMessage(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}