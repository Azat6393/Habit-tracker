package com.woynapp.aliskanlik.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habit")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String,
    val description: String,
    val started: Boolean,
    val started_date: Long,
    val days: List<Boolean>,
    val category: String
)


