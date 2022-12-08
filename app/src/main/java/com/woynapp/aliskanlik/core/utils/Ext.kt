package com.woynapp.aliskanlik.core.utils


fun Long.toDays(): Int{
    return ((this / (1000*60*60*24)) % 7).toInt()
}