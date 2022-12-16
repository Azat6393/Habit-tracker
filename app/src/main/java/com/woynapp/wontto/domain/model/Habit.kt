package com.woynapp.wontto.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habit")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String,
    val description: String,
    val started: Boolean,
    val started_date: Long? = null,
    val category: String,
    val day_size: Int,
    val uuid: String,
    val emoji: String
)


