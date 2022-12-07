package com.woynapp.aliskanlik.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


object Converters {
    @TypeConverter
    fun fromString(value: String?): List<Boolean> {
        val listType: Type = object : TypeToken<ArrayList<Boolean?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: List<Boolean?>?): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}