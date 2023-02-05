package com.woynapp.wontto.data.local.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "alarm_item"
)
data class AlarmItemDto(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val uuid: Int,
    val time: Long,
    val message: String,
    val is_mute: Boolean,
    val habit_id: String
)
