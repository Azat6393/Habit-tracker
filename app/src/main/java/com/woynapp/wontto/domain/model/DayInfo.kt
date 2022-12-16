package com.woynapp.wontto.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "day_info")
data class DayInfo (
    val habit_uuid: String,
    val day: Int,
    val type: Int,
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null
)
